package org.tlsys.twt;

import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;

import java.io.PrintStream;
import java.lang.reflect.Field;
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
        if (op instanceof VClass) {
            VClass clazz = (VClass)op;

            VClass classClass = clazz.getClassLoader().loadClass(Class.class.getName());

            NewClass classInit = new NewClass(classClass.constructors.get(0));
            classInit.arguments.add(new Const());

            VClass fieldClass = clazz.getClassLoader().loadClass(Field.class.getName());
        }
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

        addGen(This.class, (c,o,p,g)->{
            if (o.getType() != c.getCurrentClass())
                throw new RuntimeException("Not support other this type");
            p.append("this");
            return true;
        });

        addGen(Invoke.class, (c,o,p,g)->{
            InvokeGenerator icg = c.getInvokeGenerator(o.getMethod());
            if (icg != null && icg != c)
                return icg.generate(c,o,p);

            throw new RuntimeException("Invoke not supported");
        });

        addGen(VArgument.class, (c,o,p,g)->{
            p.append(o.name);
            return true;
        });
        addGen(DeclareVar.class, (c,o,p,g)->{
            p.append("var ").append(o.name);
            if (o.init != null) {
                p.append("=");
                g.operation(c, o.init, p);
            }
            return true;
        });
        addGen(GetField.class, (c,o,p,g)->{
            g.operation(c,o.getScope(),p);
            p.append(".");
            p.append(o.getField().name);
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
