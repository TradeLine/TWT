package org.tlsys.twt;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.classes.*;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

        for (VField c : cl.fields)
            compileModuls.add(c.getType());
    }

    //private static HashMap<VClassLoader, SVar> loaders = new HashMap<>();

    /*
    public static SVar getVarOfClassLoader(VClassLoader classLoader) {
        Objects.requireNonNull(classLoader, "ClassLoader is NULL");
        return Objects.requireNonNull(loaders.get(classLoader), "Can't find var for classloader " + classLoader.getName());
    }
    */

    public static SVar storage;

    @Override
    public void generate(VClassLoader projectClassLoader, CompileModuls compileModuls, PrintStream ps) throws CompileException {

        VClass classLoader = projectClassLoader.loadClass(ClassLoader.class.getName());
        VClass classClass = projectClassLoader.loadClass(Class.class.getName());
        VClass classField = projectClassLoader.loadClass(Field.class.getName());

        VClass classClassStorage = projectClassLoader.loadClass(ClassStorage.class.getName());
        VClass classClassRecord = projectClassLoader.loadClass(ClassRecord.class.getName());
        VClass classFieldRecord = projectClassLoader.loadClass(FieldRecord.class.getName());
        VClass classMethodRecord = projectClassLoader.loadClass(MethodRecord.class.getName());
        VClass classArgumentRecord = projectClassLoader.loadClass(ArgumentRecord.class.getName());
        VClass classTypeProvider = projectClassLoader.loadClass(TypeProvider.class.getName());
        VClass classString = projectClassLoader.loadClass(String.class.getName());
        VClass classBoolean = projectClassLoader.loadClass("boolean");


        addFullClass(classLoader, compileModuls);
        addFullClass(classClass, compileModuls);
        addFullClass(classField, compileModuls);
        addFullClass(classClassStorage, compileModuls);
        addFullClass(classClassRecord, compileModuls);
        addFullClass(classFieldRecord, compileModuls);
        addFullClass(classMethodRecord, compileModuls);
        addFullClass(classArgumentRecord, compileModuls);
        addFullClass(classTypeProvider, compileModuls);

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

        MainGenerationContext gc = new MainGenerationContext(classClassStorage, compileModuls);
        ICodeGenerator icg = gc.getGenerator(classClassStorage);

        storage = new SVar(classClassStorage, null);
        storage.name="storage";
        DeclareVar dv = new DeclareVar(storage);
        dv.init = new NewClass(classClassStorage.constructors.get(0));

        icg.operation(gc, dv, ps);
        ps.append(";\n");

        gc = new MainGenerationContext(classClassRecord, compileModuls);
        icg = gc.getGenerator(classClassRecord);
        VMethod storageAddMethod = classClassStorage.getMethod("add",classClassRecord);//получаем метод add класса ClassRecord
        VConstructor methodConstructor = classMethodRecord.getConstructor(classString, classString, classString);//получаем конструктор MethodRecord
        VMethod methodAddArg = classMethodRecord.getMethod("addArg", classArgumentRecord);
        VMethod classAddMethod = classClassRecord.getMethod("addMethod", classMethodRecord);
        VConstructor argumentConstructor = classArgumentRecord.getConstructor(classString, classBoolean, classTypeProvider);
        for (CompileModuls.ClassRecord cr : others) {
            VClassLoader cl = cr.getClazz().getClassLoader();
            NewClass nc = new NewClass(classClassRecord.constructors.get(0));
            nc.arguments.add(new Const(cr.getClazz().fullName, cl.loadClass(String.class.getName())));
            nc.arguments.add(new Const(cr.getClazz().alias, cl.loadClass(String.class.getName())));

            Value lastScope = nc;
            VMethod addFieldMethod = classClassRecord.getMethod("addField", cl.loadClass(String.class.getName()), cl.loadClass(String.class.getName()));
            for (VField f : cr.getClazz().fields) {

                Invoke inv = new Invoke(addFieldMethod, lastScope);
                inv.arguments.add(new Const(f.name, classString));
                inv.arguments.add(new Const(f.alias, classString));
                lastScope = inv;
            }

            for (VExecute e : cr.getExe()) {
                NewClass newMethod = new NewClass(methodConstructor);
                Value lastMethodScope = newMethod;
                newMethod.arguments.add(new Const(e.name,classString));
                if (e instanceof VConstructor)
                    newMethod.arguments.add(new Const(null,classString));
                else
                    newMethod.arguments.add(new Const(e.alias,classString));
                newMethod.arguments.add(new Const("METHOD BODY",classString));

                for (VArgument a : e.arguments) {
                    NewClass newArg = new NewClass(argumentConstructor);
                    newArg.arguments.add(new Const(a.name, classString));
                    newArg.arguments.add(new Const(a.var, classBoolean));
                    newArg.arguments.add(getClassViaTypeProvider(a.getType()));
                    Invoke addArgInvoke = new Invoke(methodAddArg, lastMethodScope);
                    addArgInvoke.arguments.add(newArg);
                    lastMethodScope = addArgInvoke;
                }

                Invoke invokeAddmethod = new Invoke(classAddMethod, lastScope);
                invokeAddmethod.arguments.add(lastMethodScope);
                lastScope = invokeAddmethod;
            }

            Invoke inv = new Invoke(storageAddMethod, storage);
            inv.arguments.add(lastScope);
            icg.operation(gc, inv, ps);
            ps.append(";\n");
        }

        /*
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
        */

        //throw new RuntimeException("generation not supported");
    }

    private static Value getClassViaTypeProvider(VClass vClass) throws VClassNotFoundException {
        VClass typeProviderClass = vClass.getClassLoader().loadClass(TypeProvider.class.getName());
        VBlock body = new VBlock();
        body.operations.add(new Return(new StaticRef(vClass)));
        Lambda lambda = new Lambda(body, typeProviderClass.methods.get(0), null);
        return lambda;
    }
}
