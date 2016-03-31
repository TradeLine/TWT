package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.Outbuffer;
import org.tlsys.lex.Invoke;
import org.tlsys.lex.declare.ArrayClass;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VExecute;
import org.tlsys.lex.declare.VMethod;
import org.tlsys.twt.*;

import java.io.PrintStream;

public class ArrayCodeGenerator extends DefaultGenerator {
    @Override
    public void generateExecute(GenerationContext context, VExecute execute, Outbuffer ps, CompileModuls moduls) throws CompileException {
        /*
        if (execute.alias.equals("get")) {
            ICodeGenerator gen = context.getGenerator(execute.getParent());
            ps.append("return ");
            gen.operation(context, execute.arguments.get(0), ps);
            ps.append(".").append(ArrayClass.GET).append("(");
            gen.operation(context, execute.arguments.get(1), ps);
            ps.append(")l");
            return;
        }

        if (execute.alias.equals("set")) {
            ICodeGenerator gen = context.getGenerator(execute.getParent());
            gen.operation(context, execute.arguments.get(0), ps);
            ps.append(".").append(ArrayClass.SET).append("(");
            gen.operation(context, execute.arguments.get(1), ps);
            ps.append(",");
            gen.operation(context, execute.arguments.get(2), ps);
            ps.append(");");
            return;
        }
        */

        if (execute.alias.equals("newInstance")) {
            VClass intClass = execute.getParent().getClassLoader().loadClass("int");
            if (execute.getArguments().get(1).getType() == intClass) {
                VClass arrayClass = execute.getParent();
                VClass classClass = arrayClass.getClassLoader().loadClass(Class.class.getName());
                VMethod getArrayClassMethod = classClass.getMethod("getArrayClass");
                ICodeGenerator cg = context.getGenerator(execute.getParent());
                ps.append("return ");
                cg.operation(context, new Invoke(getArrayClassMethod, execute.getArguments().get(0)), ps);
                ps.append(".n").append(ArrayClass.CONSTRUCTOR).append("(").append(execute.getArguments().get(1).getRuntimeName()).append(");");
                return;
            }
        }

        throw new RuntimeException("Unknown method " + execute.alias);
    }
}
