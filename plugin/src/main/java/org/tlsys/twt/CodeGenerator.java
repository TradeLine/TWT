package org.tlsys.twt;

import org.tlsys.lex.Collect;
import org.tlsys.lex.NewClass;
import org.tlsys.lex.SVar;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.*;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Objects;

public class CodeGenerator {

    private static final HashMap<Class, Object> generators = new HashMap<>();
    private static final HashMap<VClassLoader, Value> vars = new HashMap<>();
    private static long classNameIterator = 0;
    private static long methodNameIterator = 0;
    private static int fieldIterator = 0;

    public static Value getVarOfClassLoader(VClassLoader loader) {
        return vars.get(loader);
    }

    public static void renaming(VClassLoader loader) {
        String name = loader.getName();
        loader.setName(name.replace('-', '_').replace('.', '$'));

        for (VClass v : loader.classes) {
            if (v.alias == null)
                v.alias = v.fullName;
            v.fullName = "$" + Long.toString(++classNameIterator, Character.MAX_RADIX);


            for (VField f : v.fields) {
                if (f.alias == null)
                    f.alias = f.name;
                f.name = "f" + Integer.toString(++fieldIterator, Character.MAX_RADIX);
            }

            for (VMethod m : v.methods) {
                if (m.alias == null)
                    m.alias = m.getRunTimeName();
                int argIterator = 0;
                for (VArgument a : m.arguments) {
                    a.name = "a" + Integer.toString(++argIterator, Character.MAX_RADIX);
                }
                if (m.getReplace() == null)
                    m.setRuntimeName("m" + Long.toString(++methodNameIterator, Character.MAX_RADIX));
            }

            int constructIterator = 0;

            for (VConstructor m : v.constructors) {
                m.setRuntimeName("c" + Integer.toString(++constructIterator, Character.MAX_RADIX));
                int argIterator = 0;
                for (VArgument a : m.arguments) {
                    a.name = "a" + Integer.toString(++argIterator, Character.MAX_RADIX);
                }
            }
        }

        for (VClassLoader cl : loader.parents)
            renaming(cl);
    }

    /*
        private static DeclareVar createAllClassLoaders(VClass loaderClass, VClassLoader cl, PrintStream ps) throws CompileException {
            GenerationContext gc = new MyGenContext(loaderClass);
            SVar var = new SVar(loaderClass, null);
            DeclareVar dv = new DeclareVar(var);
            var.name = cl.getName();
            dv.init = new NewClass(loaderClass.constructors.get(0));
            vars.put(cl, var);

            getGeneratorFor(loaderClass).operation(gc, dv, ps);
            ps.append(";\n");



            for (VClassLoader c : cl.parents)
                createAllClassLoaders(loaderClass, c, ps);
            return dv;
        }

        public static void createAllClasses(VClass loaderClass, VClassLoader cl, PrintStream ps) throws CompileException {
            for (VClass v : cl.classes) {
                if (v == loaderClass)
                    continue;
                GenerationContext gc = new MyGenContext(loaderClass);
                getGeneratorFor(v).operation(gc, new DeclareClass(v, getVarOfClassLoader(cl)), ps);
            }

            for (VClassLoader c : cl.parents)
                createAllClasses(loaderClass, c, ps);
        }

        public static void generate(VClass clazz, String[] methods, PrintStream ps) throws CompileException {

            try {
                VClass loader = clazz.getClassLoader().loadClass(ClassLoader.class.getName());
                GenerationContext gc = new MyGenContext(loader);
                getGeneratorFor(loader).member(gc, loader, ps);

                Collect c = loader.getUsing();

                createAllClassLoaders(loader, clazz.getClassLoader(), ps);
                createAllClasses(loader, clazz.getClassLoader(), ps);


            } catch (VClassNotFoundException e) {
                throw new CompileException(e);
            }
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
    */
    private static void generateClassLoader(VClass vClass) {

    }

    /*
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
    */
}
