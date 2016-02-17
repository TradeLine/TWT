package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.lex.Operation;
import org.tlsys.lex.declare.ArrayClass;
import org.tlsys.lex.declare.VExecute;
import org.tlsys.twt.*;

import java.io.PrintStream;

public class ArrayCodeGenerator extends DefaultGenerator {
    @Override
    public void generateExecute(GenerationContext context, VExecute execute, PrintStream ps) throws CompileException {
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
        throw new RuntimeException("Unknown method " + execute.alias);
    }
}
