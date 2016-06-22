package org.tlsys.lex;

import org.tlsys.lex.declare.VClass;
import org.tlsys.twt.CompileException;

public abstract class Value extends Operation {
    private static final long serialVersionUID = -2804866196962993249L;

    public abstract VClass getType();

    @Override
    public boolean accept(OperationVisiter visiter) throws CompileException {
        return visiter.visit(this);
    }
}
