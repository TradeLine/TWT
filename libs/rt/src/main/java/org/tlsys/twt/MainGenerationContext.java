package org.tlsys.twt;

import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VExecute;

import java.util.HashMap;
import java.util.Objects;

public class MainGenerationContext implements GenerationContext {

    private static final HashMap<Class, Object> generators = new HashMap<>();

    private final VClass currentClass;
    private final CompileModuls compileModuls;

    public MainGenerationContext(VClass currentClass, CompileModuls compileModuls) {
        this.currentClass = currentClass;
        this.compileModuls = compileModuls;
    }

    @Override
    public VClass getCurrentClass() {
        return currentClass;
    }

    public static final ICodeGenerator getGeneratorFor(VClass clazz) {
        Objects.requireNonNull(clazz);
        VClass cl = clazz;
        String generator = null;
        while (cl != null) {
            if (cl.codeGenerator != null && !cl.codeGenerator.isEmpty()) {
                generator = cl.codeGenerator;
                break;
            }
            cl = cl.extendsClass;
            if (cl == null)
                break;
        }

        if (generator == null)
            throw new RuntimeException("Can't find generator for " + clazz.getRealName());
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

    public static final ICodeGenerator getGeneratorFor(VExecute method) {
        if (method.generator == null)
            return getGeneratorFor(method.getParent());

        try {
            Class genClass = method.getParent().getClassLoader().getJavaClassLoader().loadClass(method.generator);
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
    public ICodeGenerator getGenerator(VClass clazz) {
        return getGeneratorFor(clazz);
    }

    @Override
    public ICodeGenerator getGenerator(VExecute execute) {
        return getGeneratorFor(execute);
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

    @Override
    public CompileModuls getCompileModuls() {
        return compileModuls;
    }

    private long localNameIterator = 0;

    @Override
    public String genLocalName() {
        return "l"+Long.toString(++localNameIterator, Character.MAX_RADIX);
    }
}
