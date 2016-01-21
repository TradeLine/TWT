package org.tlsys.twt;

import org.tlsys.lex.Collect;
import org.tlsys.lex.Operation;
import org.tlsys.lex.declare.Member;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VClassNotFoundException;
import org.tlsys.lex.declare.VExecute;

import java.io.PrintStream;
import java.util.HashMap;

public class CodeGenerator {

    private static class MyGenContext implements GenerationContext {
        private final VClass current;

        public MyGenContext(VClass current) {
            this.current = current;
        }

        @Override
        public VClass getCurrentClass() {
            return current;
        }
    }

    public static void generate(VClass clazz, String[] methods, PrintStream ps) throws CompileException{

        try {
            VClass loader = clazz.getClassLoader().loadClass(ClassLoader.class.getName());
            getGeneratorFor(loader).member(new MyGenContext(loader), loader, ps);
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
            cl=cl.extendsClass;
            if (cl == null)
                break;
        }

        if (generator==null)
            throw new RuntimeException("Can't find generator for " + clazz.fullName);
        try {
            Class genClass = clazz.getClassLoader().getJavaClassLoader().loadClass(generator);
            if (generators.containsKey(genClass))
                return generators.get(genClass);
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

    private static final HashMap<Class, ICodeGenerator> generators = new HashMap<>();
}
