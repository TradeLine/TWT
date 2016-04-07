package org.tlsys.twt.classes;

import org.tlsys.CodeBuilder;
import org.tlsys.Outbuffer;
import org.tlsys.lex.Invoke;
import org.tlsys.lex.NewArrayItems;
import org.tlsys.lex.StaticRef;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.ArrayClass;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VExecute;
import org.tlsys.twt.*;

public class ArrayBuilderBodyGenerator extends NativeCodeGenerator implements InvokeGenerator {


    @Override
    public void generateExecute(GenerationContext context, VExecute execute, Outbuffer ps, CompileModuls moduls) throws CompileException {
        super.generateMethodStart(context, execute, ps);
        VClass arrayBuilderClass = execute.getParent();
        VClass classRecordClass = arrayBuilderClass.getClassLoader().loadClass(Class.class.getName(), execute.getPoint());
        VClass classClass = arrayBuilderClass.getClassLoader().loadClass(Class.class.getName(), execute.getPoint());
        VClass intClass = arrayBuilderClass.getClassLoader().loadClass("int", execute.getPoint());
        VClass booleanClass = arrayBuilderClass.getClassLoader().loadClass("boolean", execute.getPoint());
        ICodeGenerator cg = context.getGenerator(classClass);
        //VMethod getArrayClassMethod = classClass.getMethod("getArrayClass");

        if (execute.alias.equals("create")) {
            ps.append("{");
            ps.append("var t=");

            Value arrayClassRecord = CodeBuilder.scope(
                    execute.getArguments().get(0)
            /*
                    CodeBuilder.scope(execute.getArguments().get(0))
                    .method("getRecord")
                    .invoke(execute.getPoint()).build()
                    */
            )
                    .method("getArrayClassRecord")
                    .invoke(execute.getPoint()).build();


            Value prototypeOfArrayClass = CodeBuilder.scope(arrayClassRecord).method("getPrototype").invoke(execute.getPoint()).build();

            cg.operation(context, prototypeOfArrayClass, ps);
            ps.append(".n").append(ArrayClass.CONSTRUCTOR).append("(").append(execute.getArguments().get(1).getRuntimeName()).append(".length);");

            ps.append("for(var i=0;i<").append(execute.getArguments().get(1).getRuntimeName()).append(".length;i++){");
            ps.append("t.").append(ArrayClass.SET).append("(i,").append(execute.getArguments().get(1).getRuntimeName()).append("[i]);");
            ps.append("}");
            ps.append("return t;");
            ps.append("};");
            return;
        }
        /*
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
        */

        throw new RuntimeException("Unknown method " + execute.alias);
    }

    @Override
    public boolean generate(GenerationContext ctx, Invoke invoke, Outbuffer ps) throws CompileException {
        if (invoke.getMethod().alias.equals("create")) {
            ICodeGenerator g = ctx.getGenerator(invoke.getMethod());
            g.operation(ctx, new StaticRef(invoke.getMethod().getParent()), ps);
            ps.append(".").append(invoke.getMethod().getRunTimeName()).append("(");
            g.operation(ctx, invoke.arguments.get(0), ps);
            ps.append(",");
            ps.append("[");
            if (invoke.arguments.get(1) instanceof NewArrayItems) {
                NewArrayItems nai = (NewArrayItems)invoke.arguments.get(1);
                boolean first = true;
                for (Value v : nai.elements) {
                    if (!first)
                        ps.append(",");
                    g.operation(ctx, v, ps);
                    first = false;
                }
            } else
                throw new RuntimeException("Unknown array type");
            ps.append("])");
            return true;
        }

        throw new RuntimeException("Unknown method " + invoke.getMethod().alias);
    }
}
