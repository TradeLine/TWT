package org.tlsys.twt;

import org.tlsys.lex.Collect;
import org.tlsys.lex.MethodNotFoundException;
import org.tlsys.lex.NewClass;
import org.tlsys.lex.declare.*;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Objects;

public class CodeGenerator {

    private static final HashMap<Class, Object> generators = new HashMap<>();

    private static long classNameIterator = 0;

    public static void renaming(VClassLoader loader) {
        String name = loader.getName();
        loader.setName(name.replace('-', '_').replace('.', '$'));

        for (VClass v : loader.classes) {
            if (v.alias == null)
                v.alias = v.fullName;
            v.fullName = "$"+Long.toString(++classNameIterator, Character.MAX_RADIX);

            for (VMethod m : v.methods) {
                //if (m.alias == null)
                //    m.name =
                int argIterator = 0;
                for (VArgument a : m.arguments) {
                    a.name="a"+Integer.toString(++argIterator, Character.MAX_RADIX);
                }
            }

            for (VConstructor m : v.constructors) {
                int argIterator = 0;
                for (VArgument a : m.arguments) {
                    a.name="a"+Integer.toString(++argIterator, Character.MAX_RADIX);
                }
            }
        }

        for (VClassLoader cl : loader.parents)
            renaming(cl);
    }

    private static DeclareVar createAllClassLoaders(VClass loaderClass, VClassLoader cl, PrintStream ps) throws CompileException {
        GenerationContext gc = new MyGenContext(loaderClass);
        DeclareVar dv = new DeclareVar(loaderClass, null);
        dv.name = cl.getName();
        dv.init = new NewClass(loaderClass.constructors.get(0));

        getGeneratorFor(loaderClass).operation(gc, dv, ps);
        ps.append(";\n");

        for (VClass v : cl.classes) {
            if (v == loaderClass)
                continue;
            getGeneratorFor(loaderClass).operation(gc, new DeclareClass(v), ps);
        }

        for (VClassLoader c : cl.parents)
            createAllClassLoaders(loaderClass, c, ps);
        return dv;
    }

    public static void generate(VClass clazz, String[] methods, PrintStream ps) throws CompileException {

        try {
            VClass loader = clazz.getClassLoader().loadClass(ClassLoader.class.getName());
            GenerationContext gc = new MyGenContext(loader);
            getGeneratorFor(loader).member(gc, loader, ps);

            Collect c = loader.getUsing();

            createAllClassLoaders(loader, clazz.getClassLoader(), ps);


        } catch (VClassNotFoundException e) {
            throw new CompileException(e);
        }
        /*
        VExecute[] exe = clazz.methods.stream().filter(e->{
            for (String s : methods)
                if (e.name.equals(s) || s.equals(e.alias))
                    return true;
            return false;
        }).toArray(VExecute[]::new);


        Collect c = clazz.getUsing();

        System.out.println("" + c);
        */
    }

    private static ICodeGenerator getGeneratorFor(VClass clazz) {
        VClass cl = clazz;
        String generator = null;
        while (true) {
            if (cl.codeGenerator != null && !cl.codeGenerator.isEmpty()) {
                generator = cl.codeGenerator;
                break;
            }
            cl = cl.extendsClass;
            if (cl == null)
                break;
        }

        if (generator == null)
            throw new RuntimeException("Can't find generator for " + clazz.fullName);
        try {
            Objects.requireNonNull(clazz.getClassLoader(), "Classloader not set for " + clazz.alias);
            Objects.requireNonNull(clazz.getClassLoader().getJavaClassLoader(), "JavaClassloader not set for " + clazz.alias);
            Class genClass = clazz.getClassLoader().getJavaClassLoader().loadClass(generator);
            if (generators.containsKey(genClass))
                return (ICodeGenerator) generators.get(genClass);
            ICodeGenerator icg = (ICodeGenerator) genClass.newInstance();
            generators.put(genClass, icg);
            return icg;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void generateClassLoader(VClass vClass) {

    }

    private static class MyGenContext implements GenerationContext {
        private final VClass current;

        public MyGenContext(VClass current) {
            this.current = current;
        }

        @Override
        public VClass getCurrentClass() {
            return current;
        }

        @Override
        public ICodeGenerator getGenerator(VClass clazz) {
            return getGeneratorFor(clazz);
        }

        @Override
        public ICodeGenerator getGenerator(VExecute execute) {
            if (execute.generator == null)
                return getGenerator(execute.getParent());

            try {
                Class genClass = execute.getParent().getClassLoader().getJavaClassLoader().loadClass(execute.generator);
                if (generators.containsKey(genClass))
                    return (ICodeGenerator) generators.get(genClass);
                ICodeGenerator icg = (ICodeGenerator) genClass.newInstance();
                generators.put(genClass, icg);
                return icg;
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public InvokeGenerator getInvokeGenerator(VExecute execute) {
            if (execute.invokeGenerator == null)
                return null;

            try {
                Class genClass = execute.getParent().getClassLoader().getJavaClassLoader().loadClass(execute.invokeGenerator);
                if (generators.containsKey(genClass))
                    return (InvokeGenerator) generators.get(genClass);
                InvokeGenerator icg = (InvokeGenerator) genClass.newInstance();
                generators.put(genClass, icg);
                return icg;
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
