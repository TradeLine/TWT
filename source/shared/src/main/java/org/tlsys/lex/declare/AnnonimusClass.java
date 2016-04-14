package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.Context;
import org.tlsys.lex.Value;
import org.tlsys.sourcemap.SourcePoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class AnnonimusClass extends VClass implements Serializable {

    private ArrayList<Value> inputs = new ArrayList<>();
    private boolean useParent;

    public AnnonimusClass(Context context, VClass parent, String name, SourcePoint point) {
        super(name, context, parent, point);

    }

    public AnnonimusClass(Context context, VClass parent, Symbol.ClassSymbol classSymbol, SourcePoint point) {
        super(extractSimpleName(classSymbol), context, parent, point);
    }

    public static String extractParentClassName(Symbol.TypeSymbol c) {
        if (!isAnnonimusClass(c))
            throw new IllegalArgumentException("Type " + c.toString() + " must be annonimus class");
        String s = c.flatName().toString();
        return s.substring(0, s.indexOf("$"));
    }

    public static String extractSimpleName(Symbol.TypeSymbol c) {
        if (!isAnnonimusClass(c))
            throw new IllegalArgumentException("Type " + c.toString() + " must be annonimus class");
        String s = c.flatName().toString();
        return s.substring(s.indexOf("$") + 1);
    }

    public static boolean isAnnonimusClass(Symbol.TypeSymbol c) {
        return c instanceof Symbol.ClassSymbol && c.owner != null && (c.owner instanceof Symbol.MethodSymbol || c.owner instanceof Symbol.VarSymbol);
    }

    public ArrayList<Value> getInputs() {
        return inputs;
    }

    public boolean isUseParent() {
        return useParent;
    }

    public void setParentContext(Context context) {
        this.parentContext = Objects.requireNonNull(context);
    }

}
