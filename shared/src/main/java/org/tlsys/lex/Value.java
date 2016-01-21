package org.tlsys.lex;

import org.tlsys.lex.declare.VClass;

public abstract class Value extends Operation {
    private static final long serialVersionUID = -2804866196962993249L;

    public abstract VClass getType();
}
