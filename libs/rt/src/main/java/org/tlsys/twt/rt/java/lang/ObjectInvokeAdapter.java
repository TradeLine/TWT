package org.tlsys.twt.rt.java.lang;

import org.tlsys.CodeBuilder;
import org.tlsys.Outbuffer;
import org.tlsys.lex.Invoke;
import org.tlsys.lex.declare.VClass;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.GenerationContext;
import org.tlsys.twt.InvokeGenerator;

public class ObjectInvokeAdapter implements InvokeGenerator {
    @Override
    public boolean generate(GenerationContext ctx, Invoke invoke, Outbuffer printStream) throws CompileException {
        VClass classObject = invoke.getMethod().getParent();
        return ctx.getGenerator(classObject).operation(ctx, CodeBuilder.scopeStatic(classObject).method("getClassOfObject").arg(classObject).invoke(invoke.getStartPoint(), invoke.getEndPoint()).arg(invoke.getScope()).build(), printStream);
    }
}
