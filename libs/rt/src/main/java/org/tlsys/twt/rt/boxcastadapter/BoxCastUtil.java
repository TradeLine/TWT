package org.tlsys.twt.rt.boxcastadapter;

import org.tlsys.CodeBuilder;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.VClass;
import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.annotations.JSClass;

import java.util.Optional;

public final class BoxCastUtil {
    private BoxCastUtil() {
    }

    public static Optional<Value> objectToPrimitive(String functionName, Value from, VClass to, SourcePoint p) {
        if (byte.class.getName().equals(to.alias) ||
                short.class.getName().equals(to.alias) ||
                int.class.getName().equals(to.alias) ||
                long.class.getName().equals(to.alias) ||
                float.class.getName().equals(to.alias) ||
                double.class.getName().equals(to.alias)) {
            return Optional.of(CodeBuilder.scopeStatic(to, p).method(functionName)
                    .arg(from.getType())
                    .invoke(p)
                    .arg(from)
                    .build());
        }

        return Optional.empty();
    }

    public static Optional<Value> primitiveToString(Value from, VClass to, SourcePoint p) throws CompileException {
        VClass stringSequence = to.getClassLoader().loadClass(String.class.getName(), p);

        if (to == stringSequence || stringSequence.isParent(to)) {
            return Optional.of(CodeBuilder.scopeStatic(stringSequence).method("valueOf").arg(from.getType()).invoke(p).arg(from).build());
        }

        return Optional.empty();
    }

    public static Optional<Value> objectToString(Value from, VClass to, SourcePoint p) throws CompileException {
        VClass stringSequence = to.getClassLoader().loadClass(String.class.getName(), p);
        if (to == stringSequence || stringSequence.isParent(to)) {
            return Optional.of(CodeBuilder.scope(from).method("toString").invoke(p).build());
        }
        return Optional.empty();
    }
}
