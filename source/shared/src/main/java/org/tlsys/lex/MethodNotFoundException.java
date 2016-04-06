package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;
import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.CompileException;

import java.util.List;
import java.util.Objects;

public class MethodNotFoundException extends CompileException {

    private static final long serialVersionUID = -3914034267447714232L;

    public MethodNotFoundException(Symbol.MethodSymbol methodSymbol, SourcePoint point) {
        super(gen(methodSymbol), point);
    }

    public MethodNotFoundException(VClass clazz, String methodName, List<VClass> arguments, SourcePoint point) {
        super(gen(clazz, methodName, arguments), point);
    }

    private static String gen(Symbol.MethodSymbol methodSymbol) {
        return methodSymbol.owner.toString() + "::"+methodSymbol.toString();
    }

    private static String gen(VClass clazz, String methodName, List<VClass> arguments) {
        StringBuilder sb = new StringBuilder(Objects.requireNonNull(clazz).fullName).append("::").append(Objects.requireNonNull(methodName == null ? "<init>" : methodName)).append("(");
        boolean first = true;
        for (VClass v : Objects.requireNonNull(arguments)) {
            Objects.requireNonNull(v, "Same one argument is NULL " + clazz + " :: " + methodName);
            if (!first)
                sb.append(", ");
            sb.append(v.fullName);
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
}
