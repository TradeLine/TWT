package org.tlsys.twt.compiler;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import org.tlsys.TypeUtil;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.InvokeGenerator;
import org.tlsys.twt.annotations.CodeGenerator;
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
                return v;
            }
            VMethod v = createMethodMember(clazz, m);
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
        VConstructor con = new VConstructor(clazz, mem.sym);
        con.setModificators(toFlags(mem.getModifiers().getFlags()));
        readExecutable(mem, con);
        return con;
    }

    private static void readExecutable(JCTree.JCMethodDecl mem, VExecute m) throws VClassNotFoundException {
        getAnnatationValueClass(mem.getModifiers(), CodeGenerator.class).ifPresent(e->m.generator=e);
        getAnnatationValueClass(mem.getModifiers(), InvokeGenerator.class).ifPresent(e->m.invokeGenerator=e);
        m.setModificators(toFlags(mem.getModifiers().getFlags()));
        for (JCTree.JCVariableDecl v : mem.getParameters()) {
            //Set<Modifier> mm = v.getModifiers().getFlags();
            VClass arg = TypeUtil.loadClass(m.getParent().getClassLoader(), v.type);
            if (arg instanceof ArrayClass) {
                //VClass arg2 = vClassLoader.loadClass(v.type);
            }
            VArgument a = new VArgument(arg, v.sym);
            a.generic = v.type instanceof Type.TypeVar;
            a.var = (v.mods.flags & Flags.VARARGS) != 0;
            a.name = v.name.toString();
            m.arguments.add(a);
        }

        if (m instanceof VConstructor) {
            VClass enumClass = m.getParent().getClassLoader().loadClass(Enum.class.getName());
            if (m.getParent() != enumClass && m.getParent().isParent(enumClass)) {
                VArgument name = new VArgument(m.getParent().getClassLoader().loadClass(String.class.getName()), null);
                name.name = "name";
                m.arguments.add(name);

                VArgument ordinal = new VArgument(m.getParent().getClassLoader().loadClass("int"), null);
                ordinal.name = "ordinal";
                m.arguments.add(ordinal);
            }
        }
    }

    private static VMethod createMethodMember(VClass clazz, JCTree.JCMethodDecl mem) throws VClassNotFoundException {
        VMethod m = new VMethod(clazz, null, mem.sym);
        getAnnatationValueString(mem.getModifiers(), MethodName.class).ifPresent(e->m.alias = e);
        m.setRuntimeName(mem.getName().toString());
        m.returnType = TypeUtil.loadClass(clazz.getClassLoader(), mem.restype.type);
        readExecutable(mem, m);
        return m;
    }

    private static VField createFieldMember(VClass clazz, JCTree.JCVariableDecl fie) throws VClassNotFoundException {
        VField v = new VField(TypeUtil.loadClass(clazz.getClassLoader(), fie.type), toFlags(fie.getModifiers().getFlags()), fie.sym, clazz);
        v.name = fie.getName().toString();
        clazz.fields.add(v);
        return v;
    }

    private static int toFlags(Set<Modifier> mod) {
        int out = 0;
        if (mod.contains(Modifier.PUBLIC))
            out = out | java.lang.reflect.Modifier.PUBLIC;
        if (mod.contains(Modifier.PROTECTED))
            out = out | java.lang.reflect.Modifier.PROTECTED;
        if (mod.contains(Modifier.PRIVATE))
            out = out | java.lang.reflect.Modifier.PRIVATE;
        if (mod.contains(Modifier.STATIC))
            out = out | java.lang.reflect.Modifier.STATIC;
        if (mod.contains(Modifier.FINAL))
            out = out | java.lang.reflect.Modifier.FINAL;
        if (mod.contains(Modifier.TRANSIENT))
            out = out | java.lang.reflect.Modifier.TRANSIENT;
        if (mod.contains(Modifier.VOLATILE))
            out = out | java.lang.reflect.Modifier.VOLATILE;
        if (mod.contains(Modifier.SYNCHRONIZED))
            out = out | java.lang.reflect.Modifier.SYNCHRONIZED;
        if (mod.contains(Modifier.NATIVE))
            out = out | java.lang.reflect.Modifier.NATIVE;
        if (mod.contains(Modifier.STRICTFP))
            out = out | java.lang.reflect.Modifier.STRICT;
        return out;
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
}
