package org.tlsys.twt.expressions;

import org.tlsys.twt.members.TField;
import org.tlsys.twt.members.VClass;

public interface FieldRef extends TExpression {
    public TField getField();

    public TExpression getScope();

    @Override
    default VClass getResult() {
        return getField().getType();
    }
}
