package org.tlsys.twt.expressions;

import org.tlsys.twt.members.VClass;

public interface StaticRef extends TExpression {

    public VClass getClassRef();

    @Override
    default VClass getResult() {
        return getClassRef();
    }
}
