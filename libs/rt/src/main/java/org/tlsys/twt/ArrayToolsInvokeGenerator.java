package org.tlsys.twt;

import org.tlsys.Outbuffer;
import org.tlsys.lex.Invoke;
import org.tlsys.lex.declare.ArrayClass;
import org.tlsys.lex.declare.VClass;

public class ArrayToolsInvokeGenerator implements InvokeGenerator {
    @Override
    public boolean generate(GenerationContext ctx, Invoke invoke, Outbuffer outbuffer) throws CompileException {
        VClass clazz = invoke.getScope().getType();
        ICodeGenerator icg = ctx.getGenerator(clazz);
        icg.operation(ctx, invoke.arguments.get(0), outbuffer);
        ArrayClass ac = (ArrayClass) invoke.arguments.get(0).getType();

        outbuffer.append(".").append(ac.getField(ArrayClass.ARRAY, invoke.getStartPoint()).getRuntimeName());

        return true;
    }
}
