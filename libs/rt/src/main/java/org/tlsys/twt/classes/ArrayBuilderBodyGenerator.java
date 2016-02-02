package org.tlsys.twt.classes;

import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;
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
            ps.append("if (").append(execute.arguments.get(0).name).append(".length<=0) return null;");
            ps.append("this.").append(array.name).append("=");
            cg.operation(context, new Invoke(getArrayClassMethod, new GetField(new This(arrayBuilderClass), component)), ps);
            ps.append(".n").append(ArrayClass.CONSTRUCTOR).append("(").append(execute.arguments.get(0).name).append("[0]);");

            ps.append("if (").append(execute.arguments.get(0).name).append(".length>1){");
            ps.append("var t = ").append(execute.arguments.get(0).name).append("slice(1);");
            ps.append("var g = ").append(arrayBuilderClass.fullName).append(".n").append(arrayBuilderClass.getConstructor(intClass).name).append("(");
            cg.operation(context, new Invoke(getArrayClassMethod, new GetField(new This(arrayBuilderClass), component)), ps);
            ps.append(");");

            ps.append("for (var i = 1; i < ").append(execute.arguments.get(0).name).append("[0]; i++){");
            ps.append("this.").append(array.name).append("[i]=g.").append(arrayBuilderClass.getMethod("len", intClass.getArrayClass()).name).append("(t);");
            ps.append("}");

            ps.append("}");

            super.generateMethodEnd(context, execute, ps);
        }
        ps.append("}");
        throw new RuntimeException("Unknown method " + execute.alias);
    }
}
