package org.tlsys.twt;

import org.tlsys.Outbuffer;
import org.tlsys.lex.Invoke;
import org.tlsys.lex.StaticRef;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.VClass;

public class ApplyInvoke implements InvokeGenerator {
    @Override
    public boolean generate(GenerationContext ctx, Invoke invoke, Outbuffer ps) throws CompileException {
        VClass clazz = invoke.getScope().getType();
        ICodeGenerator icg = ctx.getGenerator(clazz);
        if (invoke.getMethod().isStatic()) {
            icg.operation(ctx, new StaticRef(clazz), ps);
        } else {
            VClass classClass = clazz.getClassLoader().loadClass(Class.class.getName(), invoke.getPoint());
            icg.operation(ctx, new StaticRef(clazz), ps);
            ps.append(".").append(classClass.getField("cons", invoke.getPoint()).getRuntimeName()).append(".prototype");
        }
        ps.append(".").append(invoke.getMethod().getRunTimeName()).append(".call(");
        icg.operation(ctx, invoke.getScope(), ps);
        for (Value v : invoke.arguments) {
            ps.append(",");
            icg.operation(ctx, v, ps);
        }
        ps.append(")");
        return true;
    }
}
