package org.tlsys.lex;

import org.tlsys.lex.members.VClass;

public interface VarRef extends TExpression {
    public LocalVar getVar();

    @Override
    default VClass getResult() {
        return getVar().getType();
    }
}
