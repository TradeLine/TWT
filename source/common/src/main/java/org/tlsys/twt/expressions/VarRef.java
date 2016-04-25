package org.tlsys.twt.expressions;

import org.tlsys.twt.members.LocalVar;
import org.tlsys.twt.members.VClass;

public interface VarRef extends TExpression {
    public LocalVar getVar();

    @Override
    default VClass getResult() {
        return getVar().getType();
    }
}
