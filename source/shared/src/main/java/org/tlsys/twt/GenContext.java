package org.tlsys.twt;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import org.tlsys.twt.annotations.MethodName;
import org.tlsys.twt.desc.*;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.NullType;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public interface GenContext {
    ClassAliaseProvider getClassAliaseProvider();

    default Executable getMethod(Class clazz, Symbol.MethodSymbol methodSymbol) throws ClassNotFoundException, NoSuchMethodException {
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

    default Executable getMethod(Symbol.MethodSymbol methodSymbol) throws ClassNotFoundException, NoSuchMethodException {
        Class<?> cl = getClassAliaseProvider().getReplacedClass(getClass(methodSymbol.owner.flatName().toString()));
        return getMethod(cl, methodSymbol);
    }

    default Executable getMethod(Class clazz, ExeDesc exeDesc) throws ClassNotFoundException, NoSuchMethodException {
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

    default Class getClass(TypeDesc type) throws ClassNotFoundException {
        Class cl = getClass(type.getType());
        int a = type.getArray();
        while (a > 0) {
            cl = Array.newInstance(cl, 0).getClass();
            a--;
        }
        return cl;
    }

    default Class getClass(Type type) throws ClassNotFoundException {
        if (type instanceof Type.ClassType) {
            Type.ClassType t = (Type.ClassType) type;
            String name = t.tsym.flatName().toString();
            return getClass(name);
        }
        if (type instanceof Type.ArrayType) {
            Type.ArrayType a = (Type.ArrayType) type;
            Class el = getClass(a.elemtype);
            String fullName = "[" + RefUtils.getClassSignName(el);
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

    default Class<?> getClass(String name) throws ClassNotFoundException {
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
            throw e;
        } catch (NoClassDefFoundError e) {
            throw e;
        }
    }

    ClassLoader getAppClassLoader();

    default Class getClass(JCTree clazz) throws ClassNotFoundException {
        return getClass(clazz.type);
    }

    default Class getClass(Symbol c) throws ClassNotFoundException {
        return getClass(c.type);
    }
}
