package org.tlsys.twt.rt.java.lang;

import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.*;
import org.tlsys.twt.classes.ClassRecord;
import org.tlsys.twt.classes.ClassStorage;
import org.tlsys.twt.classes.FieldRecord;
import org.tlsys.twt.classes.MethodRecord;

import java.io.PrintStream;

/**
 * Генератор тела функции для TClass.initArrayClass
 */
public class GenArrayClassCreateMethod extends NativeCodeGenerator {
    //TODO дописать генератор тела функции для создания класса-массива

    @Override
    public void generateExecute(GenerationContext context, VExecute execute, PrintStream ps) throws CompileException {
        VClass classClass = execute.getParent();
        VClass classClassStorage = classClass.getClassLoader().loadClass(ClassStorage.class.getName());
        VClass classClassRecord = classClass.getClassLoader().loadClass(ClassRecord.class.getName());
        VClass classMethodRecord = classClass.getClassLoader().loadClass(MethodRecord.class.getName());
        VClass classFieldRecord = classClass.getClassLoader().loadClass(FieldRecord.class.getName());
        VClass stringClass = classClass.getClassLoader().loadClass(String.class.getName());
        VClass booleanClass = classClass.getClassLoader().loadClass("boolean");

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

        SVar clazzRecord = new SVar(classClassRecord, null);
        clazzRecord.name="clazz";
        DeclareVar drecord = new DeclareVar(clazzRecord);
        VField jsNameField = classClass.getField("jsName");
        VField nameField = classClass.getField("name");
        VConstructor constructorClassRecord = classClassRecord.getConstructor(stringClass,stringClass);
        drecord.init = Generator.genClassRecord(context, classClass.getArrayClass(), execute1 -> true, () -> new NewClass(constructorClassRecord)
                .addArg(new VBinar(new Const("$", stringClass), new GetField(new This(classClass), jsNameField), stringClass, VBinar.BitType.PLUS))
                .addArg(new VBinar(new Const("[", stringClass), new GetField(new This(classClass), nameField), stringClass, VBinar.BitType.PLUS)
                ));

        Value lastScope = clazzRecord;

        ICodeGenerator cg = context.getGenerator(classClassStorage);


        cg.operation(context,
                drecord,
                ps);
        ps.append(";");

        cg.operation(context,
                new Invoke(classClassStorage.getMethod("add", classClassRecord), Generator.storage)
                        .addArg(lastScope),
                ps);
        ps.append(";");

        cg.operation(context,
                new Return(
                        new Invoke(classClassStorage.getMethod("get", classClassRecord), Generator.storage)
                                .addArg(clazzRecord)
                ),
                ps);


        ps.append("}");
        generateMethodEnd(context, execute, ps);
    }
}
