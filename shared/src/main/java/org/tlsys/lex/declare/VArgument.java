package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.SVar;

public class VArgument extends SVar {
    private static final long serialVersionUID = 8365717984255691676L;
    public boolean var;

    public VArgument() {
    }

    public VArgument(VClass clazz, Symbol.VarSymbol symbol) {
        super(clazz, symbol);
    }
    public VArgument(VClass clazz, String name, boolean var) {
        super(clazz, null);
        this.name = name;
        this.var = var;
    }
}
