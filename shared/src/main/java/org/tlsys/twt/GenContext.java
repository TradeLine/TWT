package org.tlsys.twt;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import org.tlsys.twt.annotations.MethodName;
import org.tlsys.twt.annotations.ParentClass;
import org.tlsys.twt.desc.*;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.NullType;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public interface GenContext {
    public static final Logger LOG = Logger.getLogger(GenContext.class.getName());

    public Class genClass(JCTree.JCClassDecl classDecl) throws ClassNotFoundException;

    public ClassDesc genAnnonimusClass(JCTree.JCClassDecl classDecl) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException;

    public CompileContext getCompileContext();

    public String getInvokeGenerator(String className, String method, String signature);

    public String getClassName(String name);

    public default String getClassName(Class clazz) {
        return getClassName(clazz.getName());
    }

    public default String getClassName(Type type) {
        if (type instanceof Type.ClassType)
            return getClassName(((Type.ClassType) type).tsym.flatName().toString());
        throw new RuntimeException("Not supported");
    }

    public default TypeDesc getClassType(Type type) {
        if (type instanceof Type.JCVoidType) {
            return new TypeDesc("void", 0);
        }

        if (type instanceof Type.ClassType) {
            Type.ClassType tt = (Type.ClassType)type;
            return new TypeDesc(tt.tsym.flatName().toString(), 0);
        }

        if (type instanceof Type.ArrayType) {
            Type.ArrayType a = (Type.ArrayType) type;
            TypeDesc el = getClassType(a.elemtype);
            return new TypeDesc(el.getType(), el.getArray() + 1);
        }
        if (type instanceof Type.TypeVar) {
            Type.TypeVar t = (Type.TypeVar) type;
            return getClassType(t.bound);
        }
        if (type instanceof Type.JCPrimitiveType) {
            Type.JCPrimitiveType t = (Type.JCPrimitiveType) type;
            return new TypeDesc(t.toString(), 0);
        }
        throw new RuntimeException("Not supported " + type.getClass().getName());
    }

    public ClassAliaseProvider getClassAliaseProvider();

    public default Executable getMethod(Class clazz, Symbol.MethodSymbol methodSymbol) throws ClassNotFoundException, NoSuchMethodException {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(methodSymbol);
        Objects.requireNonNull(methodSymbol.name);
        if (clazz.isEnum() && methodSymbol.name.toString().startsWith("<")) {
            return clazz.getDeclaredConstructors()[0];
        }
        boolean isConstructor = methodSymbol.name.toString().startsWith("<");
        Class depClass = isConstructor?RefUtils.getDependencyClass(clazz):null;
        ArrayList<Class> arguments = new ArrayList<>();//new Class[methodSymbol.getParameters().size()+(depClass==null?0:1)];
        int c = depClass==null?0:1;
        if (depClass != null)
            arguments.add(depClass);



        for (Symbol.VarSymbol v : methodSymbol.getParameters()) {
            arguments.add(getClass(v.type));
        }
        Executable e;
        if (methodSymbol.name.toString().startsWith("<")) {

            if (clazz.isAnonymousClass()) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field f : fields) {
                    if (f.getName().startsWith("val$"))
                        arguments.add(f.getType());
                }
            }

            try {
                e = clazz.getDeclaredConstructor(arguments.stream().toArray(Class[]::new));
            } catch (NoSuchMethodException ex) {
                Symbol.ClassSymbol cs = (Symbol.ClassSymbol)methodSymbol.owner;
                Set<Modifier> l = cs.getModifiers();
                Constructor[] cc = clazz.getDeclaredConstructors();
                Field[] f = clazz.getDeclaredFields();
                boolean anon = clazz.isAnonymousClass();
                boolean stat = java.lang.reflect.Modifier.isStatic(clazz.getModifiers());
                throw new RuntimeException(""+cc + "" + methodSymbol + "" + l + f + depClass + anon + stat, ex);
            }
        } else {
            for (Method m : clazz.getDeclaredMethods()) {
                MethodName mn = m.getAnnotation(MethodName.class);
                if (mn == null)
                    continue;
                if (mn.value().equals(methodSymbol.name.toString())) {
                    if (Arrays.equals(m.getParameterTypes(), arguments.stream().toArray(Class[]::new)))
                        return m;
                }
            }
            e = RefUtils.findMethod(clazz, methodSymbol.name.toString(), arguments.stream().toArray(Class[]::new));
        }
        return e;
    }

    public default Executable getMethod(Symbol.MethodSymbol methodSymbol) throws ClassNotFoundException, NoSuchMethodException {
        Class<?> cl = getClassAliaseProvider().getReplacedClass(getClass(((Symbol.ClassSymbol) methodSymbol.owner).flatName().toString()));
        return getMethod(cl, methodSymbol);
    }

    public default Executable getMethod(Class clazz, ExeDesc exeDesc) throws ClassNotFoundException, NoSuchMethodException {
        Class[] arguments = new Class[exeDesc.getArguments().length];
        int c = 0;
        for (ArgumentDesc a : exeDesc.getArguments()) {
            arguments[c++] = getClass(a.getType());
        }

        if (exeDesc instanceof ConstructorDesc) {
            return clazz.getDeclaredConstructor(arguments);
        }

        if (exeDesc instanceof MethodDesc) {
            try {
                MethodDesc m = (MethodDesc) exeDesc;
                return clazz.getDeclaredMethod(m.getName(), arguments);
            } catch (NoSuchMethodException e) {
                throw e;
            }
        }

        throw new RuntimeException("Unsupported type " + exeDesc.getClass().getName());
    }

    public default TypeDesc getClassType(JCTree type) {
        if (type instanceof JCTree.JCPrimitiveTypeTree)
            return new TypeDesc(type.toString(), 0);
        if (type instanceof JCTree.JCIdent)
            return getClassType(((JCTree.JCIdent) type).type);
        if (type instanceof JCTree.JCArrayTypeTree) {
            JCTree.JCArrayTypeTree a = (JCTree.JCArrayTypeTree) type;
            return new TypeDesc(getClassType(a.getType()).getType(), 1);
        }
        if (type instanceof JCTree.JCTypeApply) {
            JCTree.JCTypeApply a = (JCTree.JCTypeApply) type;
            return new TypeDesc(a.type.tsym.toString(), 0);
        }
        throw new RuntimeException("Not supported " + type.getClass().getName());
    }

    public default Class getClass(TypeDesc type) throws ClassNotFoundException {
        Class cl = getClass(type.getType());
        int a = type.getArray();
        while (a > 0) {
            cl = Array.newInstance(cl, 0).getClass();
            //String s = "[" + RefUtils.getClassSignName(cl);
            //cl = cl.forName(s);
            a--;
        }
        return cl;
    }

    public default Class getClass(Type type) throws ClassNotFoundException {
        if (type instanceof Type.ClassType) {
            Type.ClassType t = (Type.ClassType) type;
            String name = t.tsym.flatName().toString();
            return getClass(name);
        }
        if (type instanceof Type.ArrayType) {
            Type.ArrayType a = (Type.ArrayType) type;
            Class el = getClass(a.elemtype);
            String fullName = "[" + RefUtils.getClassSignName(el);
            System.out.println("try search class... " + fullName + " in " + el.getName());
            return Array.newInstance(el, 0).getClass();
        }
        if (type instanceof Type.JCPrimitiveType) {
            switch (type.getTag()) {
                case BOOLEAN:
                    return boolean.class;
                case CHAR:
                    return char.class;
                case BYTE:
                    return byte.class;
                case SHORT:
                    return short.class;
                case INT:
                    return int.class;
                case LONG:
                    return long.class;
                case FLOAT:
                    return float.class;
                case DOUBLE:
                    return double.class;
            }
        }
        if (type instanceof Type.TypeVar) {
            Type.TypeVar t = (Type.TypeVar)type;
            return getClass(t.bound);
        }
        if (type instanceof NullType) {
            return Object.class;
        }
        throw new RuntimeException("Unknown type " + type);
    }

    public default Class<?> getClass(String name) throws ClassNotFoundException {
        if (name == null || name.isEmpty())
            throw new RuntimeException("Class name not set");
        if ("boolean".equals(name))
            return boolean.class;
        if ("char".equals(name))
            return char.class;
        if ("byte".equals(name))
            return byte.class;
        if ("short".equals(name))
            return short.class;
        if ("int".equals(name))
            return int.class;
        if ("long".equals(name))
            return long.class;
        if ("float".equals(name))
            return float.class;
        if ("double".equals(name))
            return double.class;
        try {
            return getAppClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {
            System.out.println("name=" + name + ", getAppClassLoader().hash=" + getAppClassLoader().hashCode());
            throw e;
        } catch (NoClassDefFoundError e) {
            System.out.println("name=" + name + ", getAppClassLoader().hash=" + getAppClassLoader().hashCode());
            throw e;
        }
    }

    public ExpProc getExpProc();

    public ClassLoader getAppClassLoader();

    public CodeBuilder getCodeBuilder();

    public default Class getClass(JCTree clazz) throws ClassNotFoundException {
        return getClass(clazz.type);
        /*
        if (clazz.type instanceof Type.ClassType) {
            Type.ClassType c = (Type.ClassType)clazz.type;
            return getClass(c.tsym.flatName().toString());
        }

        if (clazz.type instanceof Type.JCPrimitiveType) {
            Type.JCPrimitiveType c = (Type.JCPrimitiveType)clazz.type;
            return getClass(c.toString());
        }

        LOG.info("clazz=" + clazz + " class=" + clazz.getClass().getName());
        throw new RuntimeException("Not supported " + clazz.getClass().getName());
        */
    }

    public default Class getSuperOfClass(Class clazz) throws ClassNotFoundException {
        ParentClass pc = (ParentClass) clazz.getAnnotation(ParentClass.class);
        if (pc == null)
            return clazz.getSuperclass();

        if (pc.value().isEmpty())
            return null;
        return getClass(pc.value());
    }

    public default Class[] getImplementsOfClass(Class clazz) throws ClassNotFoundException {
        ParentClass pc = (ParentClass) clazz.getAnnotation(ParentClass.class);
        if (pc == null)
            return clazz.getInterfaces();

        Class[] out = new Class[pc.implement().length];
        for (int i = 0; i < out.length; i++) {
            out[i] = getClass(pc.implement()[i]);
        }
        return out;
    }

    public default Class getClass(Symbol c) throws ClassNotFoundException {
        return getClass(c.type);
    }
}
