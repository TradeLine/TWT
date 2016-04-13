package org.tlsys.twt;

import org.tlsys.CodeBuilder;
import org.tlsys.NullClass;
import org.tlsys.Outbuffer;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.classes.*;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Objects;
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
        VClass typeValueProvider = value.getType().getClassLoader().loadClass(ValueProvider.class.getName(), null);
        VBlock body = new VBlock();
        body.add(new Return(value, null));
        Lambda lambda = new Lambda(typeValueProvider.methods.get(0), null);
        lambda.setBlock(body);
        return lambda;
    }

    public static Value getClassViaTypeProvider(VClass vClass) throws VClassNotFoundException {
        VClass typeProviderClass = vClass.getClassLoader().loadClass(TypeProvider.class.getName(), null);
        VBlock body = new VBlock();
        body.add(new Return(new ClassRecordRef(vClass, null), null));
        Lambda lambda = new Lambda(typeProviderClass.methods.get(0), null);
        lambda.setBlock(body);
        return lambda;
    }

    public static Value genClassRecord(GenerationContext gc, VClass vClass, Predicate<VExecute> exeNeed, Supplier<Value> newClass, CompileModuls moduls) throws CompileException {
        VClassLoader cl = vClass.getClassLoader();
        VClass classString = cl.loadClass(String.class.getName(), null);
        VClass objectClass = cl.loadClass(Object.class.getName(), null);

        VClass classClassRecord = cl.loadClass(ClassRecord.class.getName(), null);
        VMethod addStaticMethod = classClassRecord.getMethod("addStatic", null, objectClass);


        VClass scriptClass = cl.loadClass(Script.class.getName(), null);
        VMethod codeMethod = scriptClass.getMethodByName("code").get(0);

        VClass classBoolean = cl.loadClass("boolean", null);
        VClass classInt = cl.loadClass("int", null);
        VClass classValueProvider = cl.loadClass(ValueProvider.class.getName(), null);
        VClass classMethodRecord = cl.loadClass(MethodRecord.class.getName(), null);
        VClass classTypeProvider = cl.loadClass(TypeProvider.class.getName(), null);
        VClass classArgumentRecord = cl.loadClass(ArgumentRecord.class.getName(), null);

        VMethod methodAddArg = classMethodRecord.getMethod("addArg", null, classArgumentRecord);
        VMethod classAddMethod = classClassRecord.getMethod("addMethod", null, classMethodRecord);
        VMethod methodSetSuper = classClassRecord.getMethod("setSuper", null, classTypeProvider);
        VMethod methodAddImplement = classClassRecord.getMethod("addImplement", null, classTypeProvider);
        VConstructor methodConstructor = classMethodRecord.getConstructor(null, classString, classString, objectClass, classBoolean);//получаем конструктор MethodRecord
        VMethod addFieldMethod = classClassRecord.getMethod("addField", null, classString, classString, classTypeProvider, classString, classInt);
        VMethod setDomNodeMethod = classClassRecord.getMethod("setDomNode", null, classString);

        VConstructor argumentConstructor = classArgumentRecord.getConstructor(null, classString, classBoolean, classTypeProvider);


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

        for (VClass imp : vClass.implementsList) {
            lastScope = new Invoke(methodAddImplement, lastScope)
                    .addArg(getClassViaTypeProvider(imp));
        }

        for (VField f : vClass.getLocalFields()) {

            Invoke inv = new Invoke(addFieldMethod, lastScope)
                    .addArg(new Const(f.getRuntimeName(), classString))
                    .addArg(new Const(f.getRealName(), classString))
                    .addArg(getClassViaTypeProvider(f.getType()));

            StringOutputStream initBody = new StringOutputStream();
            hc2.operation(gc2, f.init, new Outbuffer(initBody.getStream()));

            //inv.arguments.add(getValueViaProvider((Value)f.init));
            inv.arguments.add(new Const(initBody.toString().replace("\"", "\\\""), classString));
            inv.arguments.add(new Const(f.getModificators(), classInt));
            lastScope = inv;
        }

        HashSet<VExecute> methods = new HashSet<>();
        methods.addAll(vClass.methods);
        methods.addAll(vClass.constructors);

        for (VExecute e : methods) {
            if (!exeNeed.test(e))
                continue;
            NewClass newMethod = new NewClass(methodConstructor, e.getStartPoint())
                    .addArg(new Const(e.getRunTimeName(), classString));
            Value lastMethodScope = newMethod;
            if (e instanceof VConstructor)
                newMethod.arguments.add(new Const(null, classString));
            else
                newMethod.arguments.add(new Const(e.alias, classString));

            newMethod.addArg(new CodeExe(e));
            newMethod.arguments.add(new Const(e.isStatic(), classBoolean));

            for (VArgument a : e.getArguments()) {
                NewClass newArg = new NewClass(argumentConstructor, a.getStartPoint())
                        .addArg(new Const(a.getRuntimeName(), classString))
                        .addArg(new Const(a.var, classBoolean))
                        .addArg(getClassViaTypeProvider(a.getType()));

                lastMethodScope = new Invoke(methodAddArg, lastMethodScope, e.getStartPoint(), e.getStartPoint())
                        .addArg(newArg);
            }

            Invoke invokeAddmethod = new Invoke(classAddMethod, lastScope)
                    .addArg(lastMethodScope);
            lastScope = invokeAddmethod;
        }

        for (StaticBlock b : vClass.statics) {
            StringOutputStream sos = new StringOutputStream();
            sos.getStream().append("function()");
            hc2.operation(gc, b.getBlock(), new Outbuffer(sos.getStream()));
            //sos.getStream().append("}");
            lastScope = CodeBuilder.scope(lastScope).invoke(addStaticMethod, b.getStartPoint(), b.getEndPoint()).arg(CodeBuilder.scopeStatic(scriptClass).invoke(codeMethod, null, null).arg(new NewArrayItems(objectClass.getArrayClass(), null).addEl(new Const(sos.toString(), classString))).build()).build();
            /*
            lastScope = new Invoke(addStaticMethod, lastScope)
                    .addArg(
                            CodeBuilder.scopeStatic(scriptClass).invoke(codeMethod, null).arg(new NewArrayItems(objectClass.getArrayClass(), null).addEl(new Const(sos.toString(), classString))).build()
                            //new Invoke(codeMethod, new StaticRef(scriptClass)).addArg(new NewArrayItems(objectClass.getArrayClass(), null).addEl(new Const(sos.toString(), classString)))

                    );
                    */
        }
        return lastScope;
    }

    private void addFullClass(VClass cl, CompileModuls compileModuls) {
        compileModuls.add(cl);
        for (VConstructor c : cl.constructors)
            compileModuls.add(c);

        for (VMethod c : cl.methods)
            compileModuls.add(c);

        for (VField c : cl.getLocalFields())
            compileModuls.add(c.getType());
    }

    @Override
    public void generate(VClassLoader projectClassLoader, CompileModuls compileModuls, Outbuffer ps) throws CompileException {

        VClass classClassStorage = projectClassLoader.loadClass(ClassStorage.class.getName(), null);

        storage = new SVar("S", classClassStorage, null);

        VClass classLoader = projectClassLoader.loadClass(ClassLoader.class.getName(), null);
        VClass classClass = projectClassLoader.loadClass(Class.class.getName(), null);
        VClass objectClass = projectClassLoader.loadClass(Object.class.getName(), null);
        VClass classField = projectClassLoader.loadClass(Field.class.getName(), null);
        VClass obejctsClass = projectClassLoader.loadClass(Objects.class.getName(), null);


        VClass classClassRecord = projectClassLoader.loadClass(ClassRecord.class.getName(), null);
        VClass classFieldRecord = projectClassLoader.loadClass(FieldRecord.class.getName(), null);
        VClass classMethodRecord = projectClassLoader.loadClass(MethodRecord.class.getName(), null);
        VClass classArgumentRecord = projectClassLoader.loadClass(ArgumentRecord.class.getName(), null);
        VClass classTypeProvider = projectClassLoader.loadClass(TypeProvider.class.getName(), null);
        VClass classString = projectClassLoader.loadClass(String.class.getName(), null);


        addFullClass(projectClassLoader.loadClass(Throwable.class.getName(), null), compileModuls);
        addFullClass(classLoader, compileModuls);
        addFullClass(classClass, compileModuls);
        addFullClass(classField, compileModuls);
        addFullClass(classClassStorage, compileModuls);
        addFullClass(classClassRecord, compileModuls);
        addFullClass(classFieldRecord, compileModuls);
        addFullClass(classMethodRecord, compileModuls);
        addFullClass(classArgumentRecord, compileModuls);
        addFullClass(classTypeProvider, compileModuls);
        addFullClass(objectClass, compileModuls);
        addFullClass(obejctsClass, compileModuls);
        addFullClass(projectClassLoader.loadClass(ArrayBuilder.class.getName(), null), compileModuls);


        HashSet<CompileModuls.ClassRecord> nativs = new HashSet<>();
        HashSet<CompileModuls.ClassRecord> others = new HashSet<>();
        HashSet<VClassLoader> classLoaders = new HashSet<>();
        for (CompileModuls.ClassRecord cr : compileModuls.getRecords()) {
            if (cr.getClazz() instanceof ArrayClass)
                continue;
            if (cr.getClazz() instanceof NullClass)
                continue;
            if (MainGenerationContext.getGeneratorFor(cr.getClazz()) instanceof NativeCodeGenerator)
                nativs.add(cr);
            else
                others.add(cr);
            classLoaders.add(cr.getClazz().getClassLoader());
        }

        for (CompileModuls.ClassRecord cr : nativs) {
            //cr.getClazz().addMod(new InitClassMod());
            MainGenerationContext gc = new MainGenerationContext(cr.getClazz(), compileModuls);
            ICodeGenerator icg = gc.getGenerator(cr.getClazz());
            icg.generateClass(gc, cr, ps);
        }

        MainGenerationContext gc = new MainGenerationContext(classClassStorage, compileModuls);
        ICodeGenerator icg = gc.getGenerator(classClassStorage);


        DeclareVar dv = new DeclareVar(storage, null);
        dv.init = new NewClass(classClassStorage.constructors.get(0), null);

        icg.operation(gc, dv, ps);
        ps.append(";");

        gc = new MainGenerationContext(classClassRecord, compileModuls);
        icg = gc.getGenerator(classClassRecord);
        VMethod storageAddMethod = classClassStorage.getMethod("add", null, classClassRecord);//получаем метод add класса ClassRecord


        for (CompileModuls.ClassRecord cr : others) {
            cr.getClazz().addMod(new InitClassMod());
            Value lastScope = genClassRecord(gc, cr.getClazz(),
                    ee -> cr.getExe().contains(ee),
                    () -> new NewClass(classClassRecord.constructors.get(0), null)
                            .addArg(new Const(cr.getClazz().fullName, classString))
                            .addArg(new Const(cr.getClazz().alias, classString))
                    , compileModuls);

            //gc = new MainGenerationContext(classClassStorage, compileModuls);
            Invoke inv = new Invoke(storageAddMethod, storage)
                    .addArg(lastScope);
            icg.operation(gc, inv, ps);
            ps.append(";");
        }
    }

    @Override
    public void generateInvoke(VMethod method, Outbuffer out, Value... arguments) throws CompileException {
        Invoke inv = new Invoke(method, new StaticRef(method.getParent()));
        for (Value v : arguments)
            inv.addArg(v);
        MainGenerationContext gc = new MainGenerationContext(method.getParent(), null);
        MainGenerationContext.getGeneratorFor(method).operation(gc, inv, out);
    }
}
