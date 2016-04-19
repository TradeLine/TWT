package org.tlsys.lex;

import org.tlsys.lex.members.VClass;

public interface TAssign extends TExpression {
    public TExpression getTarget();
    public TExpression getValue();

    @Override
    public default VClass getResult() {
        return getTarget().getResult();
    }
}
