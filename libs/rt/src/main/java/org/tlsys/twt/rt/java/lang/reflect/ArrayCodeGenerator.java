package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.CodeBuilder;
import org.tlsys.Outbuffer;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.ArrayClass;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VExecute;
import org.tlsys.twt.*;
import org.tlsys.twt.classes.ClassRecord;

import java.util.Objects;

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
            VClass intClass = execute.getParent().getClassLoader().loadClass("int", execute.getStartPoint());
            if (execute.getArguments().get(1).getType() == intClass) {
                VClass arrayClass = execute.getParent();

                VClass classRecordClass = arrayClass.getClassLoader().loadClass(ClassRecord.class.getName(), execute.getStartPoint());

                VClass classClass = arrayClass.getClassLoader().loadClass(Class.class.getName(), execute.getStartPoint());
                //VMethod getArrayClassMethod = classRecordClass.getMethod("getArrayClass", execute.getPoint());
                ICodeGenerator cg = context.getGenerator(execute.getParent());
                ps.append("return ");

                VClass classObject = arrayClass.getClassLoader().loadClass(Objects.class.getName(), execute.getStartPoint());
                VClass classString = arrayClass.getClassLoader().loadClass(String.class.getName(), execute.getStartPoint());


                Value arrayClazz =
                        CodeBuilder.scope(
                                CodeBuilder.scope(
                                        execute.getArguments().get(0)
                                ).method("getArrayClassRecord").invoke().build()
                        ).method("getPrototype").invoke().build();


                cg.operation(context, arrayClazz, ps);
                ps.append(".n").append(ArrayClass.CONSTRUCTOR).append("(").append(execute.getArguments().get(1).getRuntimeName()).append(");");
                return;
            }
        }

        throw new RuntimeException("Unknown method " + execute.alias);
    }
}
