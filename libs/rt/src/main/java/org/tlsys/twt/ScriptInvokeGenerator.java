package org.tlsys.twt;

import org.tlsys.lex.Const;
import org.tlsys.lex.Invoke;
import org.tlsys.lex.NewArrayItems;
import org.tlsys.lex.Value;
import org.tlsys.twt.annotations.NotCompile;

import java.io.PrintStream;
import java.lang.reflect.Executable;
import java.util.List;

@NotCompile
public class ScriptInvokeGenerator implements InvokeGenerator {
    @Override
    public boolean generate(GenerationContext ctx, Invoke invoke, PrintStream ps) throws CompileException {
        if (invoke.arguments.size() != 1)
            throw new IllegalArgumentException("Arguments of Script.code must be array of object");
        if (!(invoke.arguments.get(0) instanceof NewArrayItems)) {
            throw new IllegalArgumentException("Argument of Script.code must Array Items");
        }
        NewArrayItems items = (NewArrayItems) invoke.arguments.get(0);
        ICodeGenerator gen = ctx.getGenerator(invoke.getMethod().getParent());
        for (Value v : items.elements) {
            if (v instanceof Const) {
                Const c = (Const)v;
                if (c.getValue() != null && c.getValue() instanceof String) {
                    ps.append(c.getValue().toString());
                    continue;
                }
            }
            gen.operation(ctx, v, ps);
        }
        return true;
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
