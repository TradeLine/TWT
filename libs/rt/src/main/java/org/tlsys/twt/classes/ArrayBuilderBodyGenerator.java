package org.tlsys.twt.classes;

import org.tlsys.lex.*;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VExecute;
import org.tlsys.lex.declare.VField;
import org.tlsys.lex.declare.VMethod;
import org.tlsys.twt.*;

import java.io.PrintStream;

public class ArrayBuilderBodyGenerator extends NativeCodeGenerator {
    @Override
    public void generateClass(GenerationContext context, CompileModuls.ClassRecord record, PrintStream ps) throws CompileException {
        throw new RuntimeException("Not supported!");
    }

    @Override
    public boolean operation(GenerationContext context, Operation operation, PrintStream out) throws CompileException {
        throw new RuntimeException("Not supported!");
    }

    @Override
    public void generateExecute(GenerationContext context, VExecute execute, PrintStream ps) throws CompileException {
        super.generateMethodStart(context, execute, ps);
        VClass arrayBuilderClass = execute.getParent();
        VClass classClass = arrayBuilderClass.getClassLoader().loadClass(Class.class.getName());
        VClass intClass = arrayBuilderClass.getClassLoader().loadClass("int");
        VField component = arrayBuilderClass.getField("component");
        VClass booleanClass = arrayBuilderClass.getClassLoader().loadClass("boolean");
        ICodeGenerator cg = context.getGenerator(classClass);
        VField array = arrayBuilderClass.getField("array");
        VField level = arrayBuilderClass.getField("level");
        VMethod getArrayClassMethod = classClass.getMethod("getArrayClass");

        if (execute.alias.equals("len")) {
            ps.append("for (var i = 0; i < ").append(execute.arguments.get(0).name).append(".length; i++){");

            ps.append("}");
            new VIf(new VBinar(new Const(0, intClass),new GetField(new This(arrayBuilderClass), level), booleanClass, VBinar.BitType.GE), execute)
                    .thenBlock
            super.generateMethodEnd(context, execute, ps);
        }
        ps.append("}");
        throw new RuntimeException("Unknown method " + execute.alias);
    }
}
