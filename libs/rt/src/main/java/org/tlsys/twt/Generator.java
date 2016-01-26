package org.tlsys.twt;

import org.tlsys.lex.NewClass;
import org.tlsys.lex.SVar;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.rt.java.lang.NativeCodeGenerator;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class Generator implements MainGenerator {

    private void addFullClass(VClass cl, CompileModuls compileModuls) {
        compileModuls.add(cl);
        for (VConstructor c : cl.constructors)
            compileModuls.add(c);

        for (VMethod c : cl.methods)
            compileModuls.add(c);
    }

    private static HashMap<VClassLoader, SVar> loaders = new HashMap<>();

    public static SVar getVarOfClassLoader(VClassLoader classLoader) {
        Objects.requireNonNull(classLoader, "ClassLoader is NULL");
        return Objects.requireNonNull(loaders.get(classLoader), "Can't find var for classloader " + classLoader.getName());
    }

    @Override
    public void generate(VClassLoader projectClassLoader, CompileModuls compileModuls, PrintStream ps) throws CompileException {
        VClass classLoader = projectClassLoader.loadClass(ClassLoader.class.getName());
        VClass classClass = projectClassLoader.loadClass(Class.class.getName());
        VClass classField = projectClassLoader.loadClass(Field.class.getName());

        addFullClass(classLoader, compileModuls);
        addFullClass(classClass, compileModuls);
        addFullClass(classField, compileModuls);

        HashSet<CompileModuls.ClassRecord> nativs = new HashSet<>();
        HashSet<CompileModuls.ClassRecord> others = new HashSet<>();
        HashSet<VClassLoader> classLoaders = new HashSet<>();
        for (CompileModuls.ClassRecord cr : compileModuls.getRecords()) {
            if (cr.getClazz() instanceof ArrayClass)
                continue;
            if (MainGenerationContext.getGeneratorFor(cr.getClazz()) instanceof NativeCodeGenerator)
                nativs.add(cr);
            else
                others.add(cr);
            classLoaders.add(cr.getClazz().getClassLoader());
        }

        for (CompileModuls.ClassRecord cr : nativs) {
            MainGenerationContext gc = new MainGenerationContext(cr.getClazz(), compileModuls);
            ICodeGenerator icg = gc.getGenerator(cr.getClazz());
            icg.generateClass(gc, cr, ps);
        }

        MainGenerationContext gc = new MainGenerationContext(classLoader, compileModuls);
        ICodeGenerator icg = gc.getGenerator(classLoader);

        int clIterator = 0;
        for (VClassLoader cl : classLoaders) {
            SVar var = new SVar(classLoader, null);
            var.name = "a"+Integer.toString(++clIterator, Character.MAX_RADIX);
            DeclareVar dv = new DeclareVar(var);
            dv.init = new NewClass(classClass.constructors.get(0));
            icg.operation(gc, dv, ps);
            ps.append(";\n");
            loaders.put(cl, var);
        }

        //throw new RuntimeException("generation not supported");
    }
}
