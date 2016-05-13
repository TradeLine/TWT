package org.tlsys;

import org.tlsys.twt.ClassResolver;
import org.tlsys.twt.expressions.TConst;
import org.tlsys.twt.expressions.TExpression;
import org.tlsys.twt.generate.NameContext;
import org.tlsys.twt.members.*;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class Generator {

    private static final HashMap<Class, ExGenerator> exList = new HashMap<>();

    static {
        add(TConst.class, (e, o) -> {
            if (e.getValue() == null) {
                o.append("null");
                return true;
            }

            if (ClassResolver.resolve(e.getResult()).getRealTimeName().equals(String.class.getName())) {
                o.append("'").append(e.getValue().toString()).append("'");
                return true;
            }

            o.append(e.getValue().toString());
            return true;
        });
    }

    private static <T extends TExpression> void add(Class<T> clazz, ExGenerator<T> gen) {
        exList.put(clazz, gen);
    }

    public void generate(TExpression expression, Output output) {
        for (Map.Entry<Class, ExGenerator> e : exList.entrySet()) {
            if (e.getKey().isInstance(expression)) {
                if (e.getValue().gen(expression, output))
                    return;
            }
        }

        throw new RuntimeException("Can't find generator for " + expression.getClass().getName());
    }

    public void generateNative(VClass clazz, Output buffer, NameContext nameContext) throws IOException {
        final String CL = "a";
        buffer.append("var ").append(nameContext.getName(clazz)).append("=(function(){");
        buffer.append("function " + CL + "(){");

        clazz.getMembers().stream().filter(e -> e instanceof TField).forEach(e -> {
            TField f = (TField) e;
            buffer.append("this.").append(nameContext.getName(f)).append("=");
            generate(f.getInitValue(), buffer);
            buffer.append(";");
        });

        buffer.append("}");
        clazz.getMembers().stream().filter(e -> e instanceof VExecute).forEach(e -> {
            buffer.append(CL + ".");
            if (!Modifier.isStatic(e.getModifiers())) {
                buffer.append("prototype.");
            }

            if (e instanceof VMethod)
                buffer.append(nameContext.getName((VMethod) e));

            if (e instanceof TConstructor)
                buffer.append(nameContext.getName((TConstructor) e));
            buffer.append("(");
            VExecute ee = (VExecute) e;
            for (int i = 0; i < ee.getArguments().size(); i++) {
                TArgument arg = ee.getArguments().get(i);
                if (i != 0)
                    buffer.append(",");
                buffer.append(arg.getName());
            }
            buffer.append("){");
            buffer.append("};");
        });
        buffer.append("return " + CL + "}());");
    }

    private interface ExGenerator<T extends TExpression> {
        public boolean gen(T expression, Output output);
    }
}
