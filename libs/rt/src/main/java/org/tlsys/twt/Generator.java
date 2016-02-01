package org.tlsys.twt;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.classes.*;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Generator implements MainGenerator {

    public static SVar storage;

    //private static HashMap<VClassLoader, SVar> loaders = new HashMap<>();

    /*
    public static SVar getVarOfClassLoader(VClassLoader classLoader) {
        Objects.requireNonNull(classLoader, "ClassLoader is NULL");
        return Objects.requireNonNull(loaders.get(classLoader), "Can't find var for classloader " + classLoader.getName());
    }
    */

    private static Value getValueViaProvider(Value value) throws VClassNotFoundException {
        VClass typeValueProvider = value.getType().getClassLoader().loadClass(ValueProvider.class.getName());
        VBlock body = new VBlock();
        body.operations.add(new Return(value));
        Lambda lambda = new Lambda(body, typeValueProvider.methods.get(0), null);
        return lambda;
    }

    private static Value getClassViaTypeProvider(VClass vClass) throws VClassNotFoundException {
        VClass typeProviderClass = vClass.getClassLoader().loadClass(TypeProvider.class.getName());
        VBlock body = new VBlock();
        body.operations.add(new Return(new StaticRef(vClass)));
        Lambda lambda = new Lambda(body, typeProviderClass.methods.get(0), null);
        return lambda;
    }

    private void addFullClass(VClass cl, CompileModuls compileModuls) {
        compileModuls.add(cl);
        for (VConstructor c : cl.constructors)
            compileModuls.add(c);

        for (VMethod c : cl.methods)
            compileModuls.add(c);

        for (VField c : cl.fields)
            compileModuls.add(c.getType());
    }

    @Override
    public void generate(VClassLoader projectClassLoader, CompileModuls compileModuls, PrintStream ps) throws CompileException {

        VClass classClassStorage = projectClassLoader.loadClass(ClassStorage.class.getName());

        storage = new SVar(classClassStorage, null);
        storage.name = "storage";

        VClass classLoader = projectClassLoader.loadClass(ClassLoader.class.getName());
        VClass classClass = projectClassLoader.loadClass(Class.class.getName());
        VClass classField = projectClassLoader.loadClass(Field.class.getName());


        VClass classClassRecord = projectClassLoader.loadClass(ClassRecord.class.getName());
        VClass classFieldRecord = projectClassLoader.loadClass(FieldRecord.class.getName());
        VClass classMethodRecord = projectClassLoader.loadClass(MethodRecord.class.getName());
        VClass classArgumentRecord = projectClassLoader.loadClass(ArgumentRecord.class.getName());
        VClass classTypeProvider = projectClassLoader.loadClass(TypeProvider.class.getName());
        VClass classString = projectClassLoader.loadClass(String.class.getName());





        addFullClass(classLoader, compileModuls);
        addFullClass(classClass, compileModuls);
        addFullClass(classField, compileModuls);
        addFullClass(classClassStorage, compileModuls);
        addFullClass(classClassRecord, compileModuls);
        addFullClass(classFieldRecord, compileModuls);
        addFullClass(classMethodRecord, compileModuls);
        addFullClass(classArgumentRecord, compileModuls);
        addFullClass(classTypeProvider, compileModuls);
        addFullClass(projectClassLoader.loadClass(ArrayBuilder.class.getName()), compileModuls);


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


        DeclareVar dv = new DeclareVar(storage);
        dv.init = new NewClass(classClassStorage.constructors.get(0));

        icg.operation(gc, dv, ps);
        ps.append(";\n");

        gc = new MainGenerationContext(classClassRecord, compileModuls);
        icg = gc.getGenerator(classClassRecord);
        VMethod storageAddMethod = classClassStorage.getMethod("add", classClassRecord);//получаем метод add класса ClassRecord




        for (CompileModuls.ClassRecord cr : others) {
            Value lastScope = genClassRecord(gc, cr.getClazz(),
                    ee -> cr.getExe().contains(ee),
                    ()-> new NewClass(classClassRecord.constructors.get(0))
                                .addArg(new Const(cr.getClazz().fullName, classString))
                                .addArg(new Const(cr.getClazz().alias, classString))
                    );

            //gc = new MainGenerationContext(classClassStorage, compileModuls);
            Invoke inv = new Invoke(storageAddMethod, storage)
                    .addArg(lastScope);
            icg.operation(gc, inv, ps);
            ps.append(";\n");
        }
    }

    public static Value genClassRecord(GenerationContext gc, VClass vClass, Predicate<VExecute> exeNeed, Supplier<Value> newClass) throws CompileException {
        VClassLoader cl = vClass.getClassLoader();
        VClass classClassRecord = cl.loadClass(ClassRecord.class.getName());
        VClass classString = cl.loadClass(String.class.getName());
        VClass classBoolean = cl.loadClass("boolean");
        VClass classValueProvider = cl.loadClass(ValueProvider.class.getName());
        VClass classMethodRecord = cl.loadClass(MethodRecord.class.getName());
        VClass classTypeProvider = cl.loadClass(TypeProvider.class.getName());
        VClass classArgumentRecord = cl.loadClass(ArgumentRecord.class.getName());

        VMethod methodAddArg = classMethodRecord.getMethod("addArg", classArgumentRecord);
        VMethod classAddMethod = classClassRecord.getMethod("addMethod", classMethodRecord);
        VMethod methodSetSuper = classClassRecord.getMethod("setSuper", classTypeProvider);
        VMethod methodAddImplement = classClassRecord.getMethod("addImplement", classTypeProvider);
        VConstructor methodConstructor = classMethodRecord.getConstructor(classString, classString, classString, classBoolean);//получаем конструктор MethodRecord
        VMethod addFieldMethod = classClassRecord.getMethod("addField", classString, classString, classTypeProvider, classString, classBoolean);
        VMethod setDomNodeMethod = classClassRecord.getMethod("setDomNode", classString);
        VConstructor argumentConstructor = classArgumentRecord.getConstructor(classString, classBoolean, classTypeProvider);

        ICodeGenerator hc2 = gc.getGenerator(vClass);
        GenerationContext gc2 = new MainGenerationContext(vClass, null);
        /*
        NewClass nc = new NewClass(classClassRecord.constructors.get(0))
                .addArg(new Const(vClass.fullName, cl.loadClass(String.class.getName())))
                .addArg(new Const(vClass.alias, cl.loadClass(String.class.getName())));
*/
        Value lastScope = newClass.get();

        if (vClass.domNode != null) {
            lastScope = new Invoke(setDomNodeMethod, lastScope)
                    .addArg(new Const(vClass.domNode, classString));
        }

        if (vClass.extendsClass != null) {
            lastScope = new Invoke(methodSetSuper, lastScope)
                    .addArg(getClassViaTypeProvider(vClass.extendsClass));
        }

        for (VField f : vClass.fields) {

            Invoke inv = new Invoke(addFieldMethod, lastScope)
                    .addArg(new Const(f.name, classString))
                    .addArg(new Const(f.alias, classString))
                    .addArg(getClassViaTypeProvider(f.getType()));

            StringOutputStream initBody = new StringOutputStream();
            hc2.operation(gc2, f.init, initBody.getStream());

            //inv.arguments.add(getValueViaProvider((Value)f.init));
            inv.arguments.add(new Const(initBody.toString().replace("\"", "\\\""), classString));
            inv.arguments.add(new Const(f.isStatic(), classBoolean));
            lastScope = inv;
        }

        HashSet<VExecute> methods = new HashSet<>();
        methods.addAll(vClass.methods);
        methods.addAll(vClass.constructors);

        for (VExecute e : methods) {
            if (!exeNeed.test(e))
                continue;
            NewClass newMethod = new NewClass(methodConstructor)
                    .addArg(new Const(e.name, classString));
            Value lastMethodScope = newMethod;
            if (e instanceof VConstructor)
                newMethod.arguments.add(new Const(null, classString));
            else
                newMethod.arguments.add(new Const(e.alias, classString));


            StringOutputStream functionBody = new StringOutputStream();

            ICodeGenerator cg = gc2.getGenerator(e);
            boolean nullBody = true;
            if (cg != null) {
                if (e.block != null) {
                    cg.generateExecute(gc2, e, functionBody.getStream());
                    nullBody = false;
                }
            } else {
                if (e.block != null) {
                    hc2.generateExecute(gc2, e, functionBody.getStream());
                    nullBody = false;
                }
            }

            if (nullBody)
                newMethod.arguments.add(new Const(null, classString));
            else
                newMethod.arguments.add(new Const(functionBody.toString().replace("\"", "\\\""), classString));
            newMethod.arguments.add(new Const(e.isStatic(), classBoolean));

            for (VArgument a : e.arguments) {
                NewClass newArg = new NewClass(argumentConstructor)
                        .addArg(new Const(a.name, classString))
                        .addArg(new Const(a.var, classBoolean))
                        .addArg(getClassViaTypeProvider(a.getType()));

                lastMethodScope = new Invoke(methodAddArg, lastMethodScope)
                        .addArg(newArg);
            }

            Invoke invokeAddmethod = new Invoke(classAddMethod, lastScope)
                    .addArg(lastMethodScope);
            lastScope = invokeAddmethod;
        }

        return lastScope;
    }
}
