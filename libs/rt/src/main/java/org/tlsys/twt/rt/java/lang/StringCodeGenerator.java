package org.tlsys.twt.rt.java.lang;

import org.tlsys.Outbuffer;
import org.tlsys.lex.InstanceOf;
import org.tlsys.lex.Operation;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.DefaultGenerator;
import org.tlsys.twt.GenerationContext;

public class StringCodeGenerator extends DefaultGenerator {
    @Override
    public boolean operation(GenerationContext context, Operation op, Outbuffer out) throws CompileException {
        if (op instanceof InstanceOf) {
            InstanceOf in = (InstanceOf)op;
            if (in.getClazz() == context.getCurrentClass().getClassLoader().loadClass(String.class.getName(), in.getStartPoint())) {
                out.append("(typeof ");
                operation(context, in.getValue(), out);
                out.append("=='string')");
                return true;
            }
        }
        return super.operation(context, op, out);
    }
}
