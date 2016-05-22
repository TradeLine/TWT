package org.tlsys.twt;

import org.tlsys.CodeBuilder;
import org.tlsys.Outbuffer;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VClassLoader;
import org.tlsys.lex.declare.VMethod;
import org.tlsys.twt.annotations.NotCompile;

@NotCompile
public class ScriptInvokeGenerator implements InvokeGenerator {
    @Override
    public boolean generate(GenerationContext ctx, Invoke invoke, Outbuffer ps) throws CompileException {
        if (invoke.getMethod().alias.equals("code")) {
            if (invoke.arguments.size() != 1)
                throw new IllegalArgumentException("Arguments of Script.code must be array of object");
            if (!(invoke.arguments.get(0) instanceof NewArrayItems)) {
                throw new IllegalArgumentException("Argument of Script.code must Array Items");
            }
            NewArrayItems items = (NewArrayItems) invoke.arguments.get(0);
            ICodeGenerator gen = ctx.getGenerator(invoke.getMethod().getParent());
            for (Value v : items.elements) {
                if (v instanceof Const) {
                    Const c = (Const) v;
                    if (c.getValue() != null && c.getValue() instanceof String) {
                        ps.append(c.getValue().toString());
                        continue;
                    }
                }
                gen.operation(ctx, v, ps);
            }
            return true;
        }

        if (invoke.getMethod().alias.equals("isPrototypeOf")) {
            VClassLoader cl = invoke.getMethod().getParent().getClassLoader();
            VBinar bin = new VBinar(
                    new VBinar(invoke.arguments.get(0), new Const(null, cl.loadClass(Object.class.getName(), invoke.getStartPoint())), cl.loadClass("boolean", invoke.getStartPoint()), VBinar.BitType.EQ, null),//первый агрумент == null
                    new VBinar(invoke.arguments.get(1), new Const(null, cl.loadClass(Object.class.getName(), invoke.getStartPoint())), cl.loadClass("boolean", invoke.getStartPoint()), VBinar.BitType.EQ, null),//первый агрумент == null
                    cl.loadClass("boolean", invoke.getStartPoint()), VBinar.BitType.OR, null);//если один или оба аргумента == null
            VMethod codeMethod = invoke.getMethod().getParent().getMethod("code", invoke.getStartPoint());
            Invoke codeInvoke = new Invoke(codeMethod, new StaticRef(codeMethod.getParent()));

            NewArrayItems array = new NewArrayItems(cl.loadClass(Object.class.getName(), invoke.getStartPoint()).getArrayClass(), invoke.getStartPoint());

            array.elements.add(invoke.arguments.get(0));
            array.elements.add(new Const(" instanceof ", cl.loadClass(String.class.getName(), invoke.getStartPoint())));
            array.elements.add(invoke.arguments.get(1));
            codeInvoke.arguments.add(array);

            Conditional con = new Conditional(bin, new Const(false, cl.loadClass("boolean", invoke.getStartPoint())), codeInvoke, cl.loadClass("boolean", invoke.getStartPoint()));
            ctx.getGenerator(codeMethod.getParent()).operation(ctx, con, ps);
            return true;
        }

        if (invoke.getMethod().alias.equals("isUndefined")) {
            ps.append("(");
            ctx.getGenerator(invoke.getMethod().getParent()).operation(ctx, invoke.arguments.get(0), ps);
            ps.append("==undefined)");
            return true;
        }
        if (invoke.getMethod().alias.equals("typeOf")) {
            ps.append("(typeof ");
            ctx.getGenerator(invoke.getMethod().getParent()).operation(ctx, invoke.arguments.get(0), ps);
            ps.append(")");
            return true;
        }

        if (invoke.getMethod().alias.equals("hasOwnProperty")) {
            ps.append("(");
            ctx.getGenerator(invoke.getMethod().getParent()).operation(ctx, invoke.arguments.get(0), ps);
            ps.append(".hasOwnProperty(");
            ctx.getGenerator(invoke.getMethod().getParent()).operation(ctx, invoke.arguments.get(1), ps);
            ps.append(")");
            ps.append(")");
            return true;
        }


        if (invoke.getMethod().alias.equals("setTimeout")) {
            ps.append("setTimeout(");
            VClass callerClass = invoke.getMethod().getParent().getClassLoader().loadClass(Script.TimeoutCallback.class.getName(), invoke.getStartPoint());
            ps.append("function(){");
            ctx.getGenerator(invoke.getMethod().getParent()).operation(ctx, new Invoke(callerClass.getMethod("onTimeout", invoke.getStartPoint()), invoke.arguments.get(1)), ps);
            ps.append(";}.bind(this)");
            ps.append(",");
            ctx.getGenerator(invoke.getMethod().getParent()).operation(ctx, invoke.arguments.get(0), ps);
            ps.append(")");
            return true;
        }

        if (invoke.getMethod().alias.equals("setInterval")) {
            ps.append("setInterval(");
            VClass callerClass = invoke.getMethod().getParent().getClassLoader().loadClass(Script.TimeoutCallback.class.getName(), invoke.getStartPoint());
            ps.append("function(){");
            ctx.getGenerator(invoke.getMethod().getParent()).operation(ctx, new Invoke(callerClass.getMethod("onTimeout", invoke.getStartPoint()), invoke.arguments.get(1)), ps);
            ps.append(";}.bind(this)");
            ps.append(",");
            ctx.getGenerator(invoke.getMethod().getParent()).operation(ctx, invoke.arguments.get(0), ps);
            ps.append(")");
            return true;
        }

        if (invoke.getMethod().alias.equals("requestAnimationFrame")) {
            VClass scriptClass = invoke.getMethod().getParent();
            VClass callerClass = scriptClass.getClassLoader().loadClass(Script.FrameRequest.class.getName(), invoke.getStartPoint());
            VClass doubleClass = scriptClass.getClassLoader().loadClass(double.class.getName(), invoke.getStartPoint());
            VMethod onFrameMethod = callerClass.getMethod("onFrame", invoke.getStartPoint(), doubleClass);

            ps.append("window.requestAnimationFrame(");

            ps.append("function(_t){");
            ctx.getGenerator(invoke.getMethod().getParent()).operation(ctx, invoke.arguments.get(0), ps);
            ps.append(".");
            ps.append(onFrameMethod.getRunTimeName()).append("(_t)");
            ps.append(";}");
            ps.append(")");
            return true;
        }



        throw new RuntimeException("Unknown method " + invoke.getMethod().alias);
    }
    /*
    @Override
    public void generate(MainGenerationContext context, Class clazz, JCTree.JCExpression self, Executable method, JCTree.JCExpression[] arguments, PrintStream stream) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        if(method.getName().equals("code")) {
            for (JCTree.JCExpression e : arguments) {
                if (e instanceof JCTree.JCLiteral) {
                    JCTree.JCLiteral l = (JCTree.JCLiteral) e;
                    stream.append(String.valueOf(l.getValue()));
                    continue;
                }
                stream.append(context.getCodeBuilder().exp(e));
                //context.getExpProc().proc(context, currentClass, e, stream);
            }

            return;
        }

        if (method.getName().equals("isUndefined")) {
            stream.append("(").append(context.getCodeBuilder().exp(arguments[0])).append("==undefined").append(")");
            return;
        }

        if (method.getName().equals("typeOf")) {
            stream.append("(typeof ").append(context.getCodeBuilder().exp(arguments[0])).append(")");
            return;
        }
        throw new RuntimeException("Unknown method " + method.getName());
    }
    */
}
