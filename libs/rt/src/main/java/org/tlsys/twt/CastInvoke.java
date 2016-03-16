package org.tlsys.twt;

import org.tlsys.lex.Invoke;
import org.tlsys.lex.Value;

import java.io.PrintStream;
import java.lang.reflect.Executable;
import java.util.List;

public class CastInvoke implements InvokeGenerator {
    /*
    @Override
    public void generate(MainGenerationContext context, Class clazz, JCTree.JCExpression self, Executable method, JCTree.JCExpression[] arguments, PrintStream stream) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        stream.append(context.getCodeBuilder().exp(arguments[0]));
    }
    */

    @Override
    public boolean generate(GenerationContext ctx, Invoke invoke, PrintStream ps) throws CompileException {
        return ctx.getGenerator(ctx.getCurrentClass()).operation(ctx, invoke.arguments.get(0), ps);
    }
}
