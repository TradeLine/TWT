package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;

import java.util.ArrayList;

public class VMethod extends VExecute {
    private static final long serialVersionUID = 7352639283063310734L;
    public VMethod brigTo;
    public ArrayList<VMethod> brigFrom = new ArrayList<>();

    public VMethod(VClass parent, VMethod brigTo, Symbol.MethodSymbol symbol) {
        super(parent, symbol);
        this.brigTo = brigTo;
    }

    public VMethod() {
    }

    @Override
    public boolean isThis(String name) {
        return this.name.equals(name) || name.equals(alias);
    }
}
