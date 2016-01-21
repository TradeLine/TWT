package org.tlsys.twt;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public final class RefUtils {

    private RefUtils() {
    }

    public static Class getDependencyClass(Class clazz) {
        if (clazz.getDeclaringClass() == null)
            return null;
        /*
        try {
            return clazz.getDeclaredField("this$0").getType();
        } catch (NoSuchFieldException e) {
            return null;
        }
        */
        if (Modifier.isStatic(clazz.getModifiers()))
            return null;
        if (clazz.isAnonymousClass())
            return clazz.getEnclosingClass();
        return clazz.getDeclaringClass();
    }

    public static String getClassSignName(Class clazz) {
        if (clazz.isPrimitive()) {
            if (clazz.isAssignableFrom(short.class))
                return "S";
            if (clazz.isAssignableFrom(int.class))
                return "I";
            if (clazz.isAssignableFrom(long.class))
                return "J";
            if (clazz.isAssignableFrom(boolean.class))
                return "Z";
            if (clazz.isAssignableFrom(byte.class))
                return "B";
            if (clazz.isAssignableFrom(char.class))
                return "C";
            throw new RuntimeException("Unknown primitive");
        }
        if (clazz.isArray())
            return "[L" + getClassSignName(clazz.getComponentType());
        else
            return "L" + clazz.getName() + ";";
    }

    public static Class loadClass(String name, ClassLoader loader) throws ClassNotFoundException {
        if (name.equals("boolean"))
            return boolean.class;
        if (name.equals("char"))
            return char.class;
        if (name.equals("byte"))
            return byte.class;
        if (name.equals("short"))
            return short.class;
        if (name.equals("int"))
            return int.class;
        if (name.equals("long"))
            return long.class;
        if (name.equals("float"))
            return float.class;
        if (name.equals("double"))
            return double.class;
        return loader.loadClass(name);
    }

    public static Method findMethod(Class clazz, String name, Class[] arguments) throws NoSuchMethodException {
        Class t = clazz;
        while (t != null) {
            try {
                return t.getDeclaredMethod(name, arguments);
            } catch (NoSuchMethodException e) {
                t = t.getSuperclass();
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(clazz.getName()).append(".").append(name).append("(");
        boolean first = true;
        for (Class c : arguments) {
            if (!first)
                sb.append(", ");
            sb.append(c.getCanonicalName());
            first = false;
        }
        sb.append(")");
        throw new NoSuchMethodException(sb.toString());
    }

    public static Optional<Method> getBrigeMethodFor(Method method) {
        ArrayList<Method> methods = new ArrayList<>(Arrays.asList(method.getDeclaringClass().getDeclaredMethods()));
        methods.removeIf(e -> {
            return e == method || !e.isBridge() || !e.getName().equals(method.getName());
        });
        if (methods.isEmpty())
            return Optional.empty();
        if (methods.size() != 1)
            throw new RuntimeException("More one brige methods for " + method.toString());
        return Optional.of(methods.get(0));
    }

    public static ConstructorArgumentDescription getArgumentDescription(Constructor constructor) {
        Class clazz = constructor.getDeclaringClass();
        Class dep = getDependencyClass(clazz);
        Parameter[] params = constructor.getParameters();
        int min = 0;
        int max = params.length - 1;

        if (clazz != null) {
            min++;
        }
        ArrayList<Class> args = new ArrayList<>();
        ArrayList<Class> vals = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().startsWith("val$")) {
                max--;
                vals.add(field.getType());
            }
        }

        for (int i = min; i <= max; i++) {
            args.add(params[i].getType());
        }
        return new ConstructorArgumentDescription(dep, args.stream().toArray(Class[]::new), vals.stream().toArray(Class[]::new));
    }

    public static class ConstructorArgumentDescription {
        private final Class dependency;
        private final Class[] arguments;
        private final Class[] values;

        public ConstructorArgumentDescription(Class dependency, Class[] arguments, Class[] values) {
            this.dependency = dependency;
            this.arguments = arguments;
            this.values = values;
        }

        public Class getDependency() {
            return dependency;
        }

        public Class[] getArguments() {
            return arguments;
        }

        public Class[] getValues() {
            return values;
        }
    }

}
