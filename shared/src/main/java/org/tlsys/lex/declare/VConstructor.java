package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;

public class VConstructor extends VExecute {
    private static final long serialVersionUID = 6381674695841109642L;

    public VConstructor(VClass parent, Symbol.MethodSymbol symbol) {
        super(parent, symbol);
        try {
            returnType = parent.getClassLoader().loadClass("void");
        } catch (VClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isThis(String name) {
        return false;
    }

    public VConstructor() {
    }

    @Override
    public String toString() {
        return "VConstructor{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", parent=" + getParent() +
                '}';
    }
}
