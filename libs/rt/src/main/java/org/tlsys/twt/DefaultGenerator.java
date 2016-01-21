package org.tlsys.twt;

import org.tlsys.lex.Const;
import org.tlsys.lex.Operation;
import org.tlsys.lex.Return;
import org.tlsys.lex.declare.Member;

import java.io.PrintStream;
import java.util.HashMap;

public class DefaultGenerator implements ICodeGenerator {

    private interface Gen<T> {
        public boolean gen(GenerationContext ctx, T op, PrintStream ps, ICodeGenerator gen) throws CompileException;
    }

    public static <T> void addGen(Class<T> clazz, Gen<T> gen) {
        generators.put(clazz, gen);
    }

    private static HashMap<Class, Gen> generators = new HashMap<>();

    @Override
    public boolean member(GenerationContext ctx, Member op, PrintStream ps) throws CompileException {
        throw new RuntimeException("Not supported yet " + op.getClass().getName());
    }

    static {
        addGen(Const.class, (c,o,p,g)->{
            if (o.getValue() == null) {
                p.append("null");
                return true;
            }
            if (o.getValue() instanceof String) {
                p.append("\"").append(o.getValue().toString()).append("\"");
                return true;
            }
            p.append(o.getValue().toString());
            return true;
        });

        addGen(Return.class, (c,o,p,g)->{
            p.append("return");
            if (o.getValue() != null) {
                p.append(" ");
                g.operation(c, o.getValue(), p);
            }
            return true;
        });
    }

    @Override
    public boolean operation(GenerationContext context, Operation op, PrintStream out) throws CompileException {
        Gen g = generators.get(op.getClass());
        if (g != null) {
            return g.gen(context, op, out, this);
        }
        throw new RuntimeException("Not supported yet " + op.getClass().getName());
    }
}
