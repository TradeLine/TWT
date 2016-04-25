package org.tlsys.twt.expressions;

import org.tlsys.twt.members.VClass;

public interface This extends TExpression {
    public VClass getClassRef();

    @Override
    default VClass getResult() {
        return getClassRef();
    }
}
