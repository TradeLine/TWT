package org.tlsys.twt.rt.java.lang;

import org.tlsys.lex.InstanceOf;
import org.tlsys.lex.Operation;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.DefaultGenerator;
import org.tlsys.twt.GenerationContext;

import java.io.PrintStream;

public class StringCodeGenerator extends DefaultGenerator {
    @Override
    public boolean operation(GenerationContext context, Operation op, PrintStream out) throws CompileException {
        if (op instanceof InstanceOf) {
            InstanceOf in = (InstanceOf)op;
            if (in.getClazz() == context.getCurrentClass().getClassLoader().loadClass(String.class.getName())) {
                out.append("(typeof ");
                operation(context, in.getValue(), out);
                out.append("=='string')");
                return true;
            }
        }
        return super.operation(context, op, out);
    }
}
