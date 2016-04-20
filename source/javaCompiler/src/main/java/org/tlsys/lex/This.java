package org.tlsys.lex;

import org.tlsys.lex.members.VClass;

public interface This extends TExpression {
    public VClass getClassRef();

    @Override
    default VClass getResult() {
        return getClassRef();
    }
}
