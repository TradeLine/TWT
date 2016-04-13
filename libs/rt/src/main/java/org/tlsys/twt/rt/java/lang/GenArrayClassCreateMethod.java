package org.tlsys.twt.rt.java.lang;

import org.tlsys.CodeBuilder;
import org.tlsys.Outbuffer;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.*;
import org.tlsys.twt.classes.ClassRecord;
import org.tlsys.twt.classes.ClassStorage;
import org.tlsys.twt.classes.FieldRecord;
import org.tlsys.twt.classes.MethodRecord;

/**
 * Генератор тела функции для TClass.initArrayClass
 */
public class GenArrayClassCreateMethod extends NativeCodeGenerator {
    //TODO дописать генератор тела функции для создания класса-массива

    @Override
    public void generateExecute(GenerationContext context, VExecute execute, Outbuffer ps, CompileModuls moduls) throws CompileException {
        VClass classClass = execute.getParent();
        VClass classClassStorage = classClass.getClassLoader().loadClass(ClassStorage.class.getName(), execute.getPoint());
        VClass classClassRecord = classClass.getClassLoader().loadClass(ClassRecord.class.getName(), execute.getPoint());
        VClass classMethodRecord = classClass.getClassLoader().loadClass(MethodRecord.class.getName(), execute.getPoint());
        VClass classFieldRecord = classClass.getClassLoader().loadClass(FieldRecord.class.getName(), execute.getPoint());
        VClass stringClass = classClass.getClassLoader().loadClass(String.class.getName(), execute.getPoint());
        VClass booleanClass = classClass.getClassLoader().loadClass("boolean", execute.getPoint());

        generateMethodStart(context, execute, ps);
        ps.append("{");
        /*
        SVar clazz = new SVar(classClass, null);
        clazz.name="clazz";
        DeclareVar dv = new DeclareVar(clazz);
        NewClass nc = new NewClass(classClass.getConstructor(stringClass));
        dv.init = nc;
        nc.arguments.add(new VBinar(new Const("[",stringClass), new GetField(new This(classClass), classClass.getField("name")), stringClass, VBinar.BitType.PLUS));
        */

        SVar clazzRecord = new SVar("clazz", classClassRecord, execute);
        DeclareVar drecord = new DeclareVar(clazzRecord, null);
        VField jsNameField = classClass.getField("jsName", execute.getPoint());
        VField nameField = classClass.getField("name", execute.getPoint());
        ArrayClass ac = classClass.getArrayClass();

        ac.set.generator = ArrayBodyGenerator.class.getName();
        ac.constructor.generator = ArrayBodyGenerator.class.getName();
        ac.get.generator = ArrayBodyGenerator.class.getName();

        VConstructor constructorClassRecord = classClassRecord.getConstructor(execute.getPoint(), stringClass, stringClass);
        drecord.init = Generator.genClassRecord(context, ac, execute1 -> true, () -> new NewClass(constructorClassRecord, null)
                .addArg(new VBinar(new Const("$", stringClass), new GetField(new This(classClass), jsNameField, null), stringClass, VBinar.BitType.PLUS, null))
                .addArg(new VBinar(new Const("[", stringClass), new GetField(new This(classClass), nameField, null), stringClass, VBinar.BitType.PLUS, null)
                ), moduls);

        drecord.init = CodeBuilder.scope((Value) drecord.init).method("setComponentType").arg(classClassRecord).invoke().arg(
                new This(classClassRecord, null)).build();

        Value lastScope = clazzRecord;

        ICodeGenerator cg = context.getGenerator(classClassStorage);


        cg.operation(context,
                drecord,
                ps);
        ps.append(";");

        cg.operation(context,
                CodeBuilder
                        .scope(Generator.storage)
                        .method("add")
                        .arg(classClassRecord)
                        .invoke()
                        .arg(lastScope)
                        .build(),
                ps);
        /*
        cg.operation(context,
                new Invoke(classClassStorage.getMethod("add", classClassRecord), Generator.storage)
                        .addArg(lastScope),
                ps);
                */
        ps.append(";");

        cg.operation(context, new Return(clazzRecord, null), ps);
        /*

        cg.operation(context,
                new Return(
                        new Invoke(classClassStorage.getMethod("get", classClassRecord), Generator.storage)
                                .addArg(clazzRecord)
                        , null),
                ps);
                */

        ps.append("}");
        generateMethodEnd(context, execute, ps);
    }
}
