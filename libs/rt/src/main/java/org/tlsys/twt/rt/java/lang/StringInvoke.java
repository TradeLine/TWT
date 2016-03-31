package org.tlsys.twt.rt.java.lang;

import org.tlsys.Outbuffer;
import org.tlsys.lex.Invoke;
import org.tlsys.lex.StaticRef;
import org.tlsys.lex.declare.VMethod;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.GenerationContext;
import org.tlsys.twt.ICodeGenerator;
import org.tlsys.twt.InvokeGenerator;

import java.io.PrintStream;

public class StringInvoke implements InvokeGenerator {
    /*
    @Override
    public void generate(MainGenerationContext ctx, Class clazz, JCTree.JCExpression self, Executable method, JCTree.JCExpression[] arguments, PrintStream ps) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {


        if (method.getName().equals("valueOf")) {
            ps.append("(").append(ctx.getCodeBuilder().exp(arguments[0])).append("==null?'null':").append(ctx.getCodeBuilder().exp(arguments[0])).append(".toString())");
            return;
        }

        throw new RuntimeException("Unknown method " + method.getName());
    }
    */

    @Override
    public boolean generate(GenerationContext ctx, Invoke invoke, Outbuffer ps) throws CompileException {

        ICodeGenerator stringCG = ctx.getGenerator(invoke.getMethod().getParent());

        if (invoke.getMethod().alias.equals("equals")) {
            ps.append("(");
            stringCG.operation(ctx, invoke.getSelf(), ps);
            ps.append("==");
            ctx.getGenerator(invoke.arguments.get(0).getType()).operation(ctx, invoke.arguments.get(0), ps);
            ps.append(")");
            return true;
        }

        if (invoke.getMethod().alias.equals("substring")) {
            stringCG.operation(ctx, invoke.getSelf(), ps);
            ps.append(".substring(");
            ctx.getGenerator(invoke.arguments.get(0).getType()).operation(ctx, invoke.arguments.get(0), ps);
            if (invoke.arguments.size() > 1) {
                ps.append(",");
                ctx.getGenerator(invoke.arguments.get(1).getType()).operation(ctx, invoke.arguments.get(1), ps);
            }
            ps.append(")");
            return true;
        }

        if (invoke.getMethod().alias.equals("indexOf")) {
            stringCG.operation(ctx, invoke.getSelf(), ps);
            ps.append(".indexOf(");
            ctx.getGenerator(invoke.arguments.get(0).getType()).operation(ctx, invoke.arguments.get(0), ps);
            if (invoke.arguments.size() > 1) {
                ps.append(",");
                ctx.getGenerator(invoke.arguments.get(1).getType()).operation(ctx, invoke.arguments.get(1), ps);
            }
            ps.append(")");
            return true;
        }

        if (invoke.getMethod().alias.equals("lastIndexOf")) {
            stringCG.operation(ctx, invoke.getSelf(), ps);
            ps.append(".lastIndexOf(");
            ctx.getGenerator(invoke.arguments.get(0).getType()).operation(ctx, invoke.arguments.get(0), ps);
            if (invoke.arguments.size() > 1) {
                ps.append(",");
                ctx.getGenerator(invoke.arguments.get(1).getType()).operation(ctx, invoke.arguments.get(1), ps);
            }
            ps.append(")");
            return true;
        }

        if (invoke.getMethod().alias.equals("isEmpty")) {
            ps.append("(");
            stringCG.operation(ctx, invoke.getSelf(), ps);
            ps.append(".length==0)");
            return true;
        }

        if (invoke.getMethod().alias.equals("length")) {
            stringCG.operation(ctx, invoke.getSelf(), ps);
            ps.append(".length");
            return true;
        }

        if (invoke.getMethod().alias.equals("endsWith")) {
            ps.append("(");
            stringCG.operation(ctx, invoke.getSelf(), ps);
            ps.append(".indexOf(");
            stringCG.operation(ctx, invoke.arguments.get(0), ps);
            ps.append(",");
            stringCG.operation(ctx, invoke.getSelf(), ps);
            ps.append(".length - ");
            stringCG.operation(ctx, invoke.arguments.get(0), ps);
            ps.append(".length) !== -1)");
            return true;
        }

        if (invoke.getMethod().alias.equals("split")) {
            if (invoke.arguments.size() == 1) {
                /*
                VClass stringClass = invoke.getMethod().getParent();
                VClass objectClass = stringClass.getClassLoader().loadClass(Object.class.getName());
                VClass arrayClass = stringClass.getClassLoader().loadClass(JArray.class.getName());
                VClass classClass = stringClass.getClassLoader().loadClass(Class.class.getName());
                VMethod fromJSArrayMethod = arrayClass.getMethod("fromJSArray", objectClass, classClass);
                //return JArray.fromJSArray(Script.code(str,".split(new RegExp(",regexp,"))"), String.class);
                new Return(new Invoke(fromJSArrayMethod, new StaticRef(arrayClass)).addArg());
                */

                VMethod staticSplitMethod = invoke.getMethod().getParent().getMethod("split", invoke.getMethod().getParent(), invoke.getMethod().getParent());
                return stringCG.operation(ctx, new Invoke(staticSplitMethod, new StaticRef(invoke.getMethod().getParent())).addArg(invoke.getSelf()).addArg(invoke.arguments.get(0)), ps);
            }
        }



        /*
        if (invoke.getMethod().alias.equals("charAt")) {
            String t = ctx.getCodeBuilder().exp(self) + ".charAt(" + ctx.getCodeBuilder().exp(arguments[0]) + ")";
            ps.append(ctx.getCodeBuilder().newInst(Tchar.class, Tchar.class.getConstructor(String.class), t));
            return;
        }
        */

        throw new RuntimeException("Method " + invoke.getMethod().alias + " not processed");
    }
}
