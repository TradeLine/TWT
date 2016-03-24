package org.tlsys.twt.compiler;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import org.tlsys.TypeUtil;
import org.tlsys.lex.Cast;
import org.tlsys.lex.Const;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.ICastAdapter;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.ForceInject;
import org.tlsys.twt.annotations.InvokeGen;
import org.tlsys.twt.annotations.MethodName;

import javax.lang.model.element.Modifier;
import java.util.Optional;
import java.util.Set;

public class CompilerTools {

    public static Member createMember(VClass clazz, JCTree decl) throws VClassNotFoundException {
        if (decl instanceof JCTree.JCMethodDecl) {
            JCTree.JCMethodDecl m = (JCTree.JCMethodDecl) decl;
            if (m.name.toString().equals("<init>")) {
                VConstructor v = createConstructorMember(clazz, m);
                clazz.constructors.add(v);
                v.force = isAnnatationExist(m.getModifiers(), ForceInject.class);
                return v;
            }
            VMethod v = createMethodMember(clazz, m);
            v.force = isAnnatationExist(m.getModifiers(), ForceInject.class);
            clazz.methods.add(v);
            return v;
        }

        if (decl instanceof JCTree.JCVariableDecl) {
            return createFieldMember(clazz, (JCTree.JCVariableDecl) decl);
        }
        if (decl instanceof JCTree.JCClassDecl) {
            return null;
        }

        if (decl instanceof JCTree.JCBlock) {
            StaticBlock sb = new StaticBlock(clazz);
            clazz.statics.add(sb);
            return sb;
        }

        throw new RuntimeException("Not supported " + decl.getClass().getName() + " " + decl);
    }

    private static VConstructor createConstructorMember(VClass clazz, JCTree.JCMethodDecl mem) throws VClassNotFoundException {
        VConstructor con = new VConstructor(clazz);
        con.setModificators(toFlags(mem.getModifiers()));
        readExecutable(mem, con);
        //VClass enumClass = clazz.getClassLoader().loadClass(Enum.class.getName());
        return con;
    }

    private static void readExecutable(JCTree.JCMethodDecl mem, VExecute m) throws VClassNotFoundException {
        getAnnatationValueClass(mem.getModifiers(), CodeGenerator.class).ifPresent(
                e -> m.generator = e
        );
        getAnnatationValueClass(mem.getModifiers(), InvokeGen.class).ifPresent(e -> m.invokeGenerator = e);
        m.setModificators(toFlags(mem.getModifiers()));
        for (JCTree.JCVariableDecl v : mem.getParameters()) {
            //Set<Modifier> mm = v.getModifiers().getFlags();
            VClass arg = TypeUtil.loadClass(m.getParent().getClassLoader(), v.type);
            if (arg instanceof ArrayClass) {
                //VClass arg2 = vClassLoader.loadClass(v.type);
            }
            VArgument a = new VArgument(v.name.toString(),arg, (v.mods.flags & Flags.VARARGS) != 0, v.type instanceof Type.TypeVar, m, null);
            m.addArg(a);
        }



        if (m instanceof VConstructor) {
            VClass enumClass = m.getParent().getClassLoader().loadClass(Enum.class.getName());
            if (m.getParent() != enumClass && m.getParent().isParent(enumClass)) {
                VArgument name = new VArgument("name", m.getParent().getClassLoader().loadClass(String.class.getName()), false, false, m, null);
                m.addArg(name);

                VArgument ordinal = new VArgument("ordinal", m.getParent().getClassLoader().loadClass("int"), false, false, m, null);
                m.addArg(ordinal);
            }

            /*
            //Если конструктор этого класса имеет родителя, при этом не является
            //интерфейсом,enum'мом и не обявлен как статический, то добавляем
            //его аргумент на this родителя
            if (m.getParent().getParent() != null
                    && !java.lang.reflect.Modifier.isInterface(m.getParent().getModificators())
                    && !java.lang.reflect.Modifier.isStatic(m.getParent().getModificators())
                    && !m.getParent().isParent(enumClass)) {

                VArgument parent = new VArgument("this$0",m.getParent().getParent(), false, false, null);
                m.arguments.add(0, parent);
            }
            */
        }

    }

    private static VMethod createMethodMember(VClass clazz, JCTree.JCMethodDecl mem) throws VClassNotFoundException {
        VMethod m = new VMethod(clazz, null);
        m.setRuntimeName(mem.getName().toString());
        m.alias = m.getRunTimeName();
        getAnnatationValueString(mem.getModifiers(), MethodName.class).ifPresent(e -> m.alias = e);
        m.returnType = TypeUtil.loadClass(clazz.getClassLoader(), mem.restype.type);
        readExecutable(mem, m);
        return m;
    }

    private static VField createFieldMember(VClass clazz, JCTree.JCVariableDecl fie) throws VClassNotFoundException {
        VField v = new VField(fie.getName().toString(),TypeUtil.loadClass(clazz.getClassLoader(), fie.type), toFlags(fie.getModifiers()), clazz);
        clazz.addLocalField(v);
        return v;
    }

    public static int toFlags(JCTree.JCModifiers m) {
        int out = 0;
        Set<Modifier> mod = m.getFlags();

        if ((m.flags & 512L) != 0)
            out = out | java.lang.reflect.Modifier.INTERFACE;

        if (mod.contains(Modifier.PUBLIC)) {
            out = out | java.lang.reflect.Modifier.PUBLIC;
        }

        if (mod.contains(Modifier.ABSTRACT)) {
            out = out | java.lang.reflect.Modifier.ABSTRACT;
        }

        if (mod.contains(Modifier.PROTECTED)) {
            out = out | java.lang.reflect.Modifier.PROTECTED;
        }
        if (mod.contains(Modifier.PRIVATE)) {
            out = out | java.lang.reflect.Modifier.PRIVATE;
        }
        if (mod.contains(Modifier.STATIC)) {
            out = out | java.lang.reflect.Modifier.STATIC;
        }
        if (mod.contains(Modifier.FINAL)) {
            out = out | java.lang.reflect.Modifier.FINAL;
        }
        if (mod.contains(Modifier.TRANSIENT)) {
            out = out | java.lang.reflect.Modifier.TRANSIENT;
        }
        if (mod.contains(Modifier.VOLATILE)) {
            out = out | java.lang.reflect.Modifier.VOLATILE;
        }
        if (mod.contains(Modifier.SYNCHRONIZED)) {
            out = out | java.lang.reflect.Modifier.SYNCHRONIZED;
        }
        if (mod.contains(Modifier.NATIVE)) {
            out = out | java.lang.reflect.Modifier.NATIVE;
        }
        if (mod.contains(Modifier.STRICTFP)) {
            out = out | java.lang.reflect.Modifier.STRICT;
        }
        return out;
    }

    public static boolean isAnnatationExist(JCTree.JCModifiers modifiers, Class annatationClass) {
        for (JCTree.JCAnnotation an : modifiers.getAnnotations()) {
            if (an.type.toString().equals(annatationClass.getName())) {
                return true;
            }
        }
        return false;
    }

    public static Optional<String> getAnnatationValueClass(JCTree.JCModifiers modifiers, Class annatationClass) {
        for (JCTree.JCAnnotation an : modifiers.getAnnotations()) {
            if (an.type.toString().equals(annatationClass.getName())) {
                JCTree.JCAssign a = (JCTree.JCAssign) an.getArguments().get(0);
                JCTree.JCFieldAccess val = (JCTree.JCFieldAccess) a.getExpression();
                String codeGenerator = "" + val.type.toString().substring(Class.class.getName().length() + 1);//val.selected.toString();
                return Optional.of(codeGenerator.substring(0, codeGenerator.length() - 1));
            }
        }
        return Optional.empty();
    }

    public static Optional<String> getAnnatationValueString(JCTree.JCModifiers modifiers, Class annatationClass) {
        for (JCTree.JCAnnotation an : modifiers.getAnnotations()) {
            if (an.type.toString().equals(annatationClass.getName())) {
                JCTree.JCAssign a = (JCTree.JCAssign) an.getArguments().get(0);
                JCTree.JCLiteral val = (JCTree.JCLiteral) a.getExpression();
                return Optional.of((String) val.getValue());
            }
        }
        return Optional.empty();
    }

    public static Value cast(Value value, VClass type) throws CompileException {
        //if (true)
        //    return value;
        if (value.getType() == type || value.getType().isParent(type))
            return value;

        if (value instanceof Const && ((Const)value).getValue()==null)
            return value;

        System.out.println("=======================CAST " + value.getType().getRealName() + "@" + value.getType().hashCode() + "["+value.getType().getClassLoader().getName() + "] =>>>> " + type.getRealName() + "@" + type.hashCode() + "["+type.getClassLoader().getName()+"]");
        return new Cast(type, value);
        /*

        ICastAdapter ica = getCastAdapter(value.getType());
        return ica.cast(value, type);
        */
    }

    private static ICastAdapter getCastAdapter(VClass clazz) {
        VClass cl = clazz;
        while (cl != null) {
            if (cl.castGenerator != null && !cl.castGenerator.isEmpty()) {
                try {

                    return (ICastAdapter)cl.getJavaClass().getClassLoader().loadClass(cl.castGenerator).newInstance();

                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }
            cl = cl.extendsClass;
        }
        throw new RuntimeException("Can't find cast adapter for " + clazz.getRealName());
    }


}
