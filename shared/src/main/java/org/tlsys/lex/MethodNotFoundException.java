package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;
import org.tlsys.twt.CompileException;

import java.util.List;
import java.util.Objects;

public class MethodNotFoundException extends CompileException {

    private static String gen(Symbol.MethodSymbol methodSymbol) {
        return methodSymbol.owner.toString() + "::"+methodSymbol.toString();
    }

    private static String gen(VClass clazz, String methodName, List<VClass> arguments) {
        if (clazz == null)
            System.out.println("123");
        if (methodName == null)
            System.out.println("123");
        StringBuilder sb = new StringBuilder(Objects.requireNonNull(clazz).fullName).append("::").append(Objects.requireNonNull(methodName)).append("(");
        boolean first = true;
        for (VClass v : Objects.requireNonNull(arguments)) {
            if (!first)
                sb.append(", ");
            sb.append(v.fullName);
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }

    public MethodNotFoundException(Symbol.MethodSymbol methodSymbol) {
        super(gen(methodSymbol));
    }

    public MethodNotFoundException(VClass clazz, String methodName, List<VClass> arguments) {
        super(gen(clazz, methodName, arguments));
    }
}
