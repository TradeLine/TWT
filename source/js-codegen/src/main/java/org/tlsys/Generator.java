package org.tlsys;

import org.tlsys.twt.expressions.TConst;
import org.tlsys.twt.expressions.TExpression;

import java.io.IOException;
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

            if (e.getResult().getRealTimeName().equals(String.class.getName())) {
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

    public void generate(TExpression expression, Output output) throws IOException {
        for (Map.Entry<Class, ExGenerator> e : exList.entrySet()) {
            if (e.getKey().isInstance(expression)) {
                if (e.getValue().gen(expression, output))
                    return;
            }
        }

        throw new RuntimeException("Can't find generator for " + expression.getClass().getName());
    }

    private interface ExGenerator<T extends TExpression> {
        public boolean gen(T expression, Output output) throws IOException;
    }
}
