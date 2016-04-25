package org.tlsys.twt.expressions;

import org.tlsys.twt.members.VClass;

public interface TAssign extends TExpression {
    public TExpression getTarget();
    public TExpression getValue();

    @Override
    public default VClass getResult() {
        return getTarget().getResult();
    }
}
