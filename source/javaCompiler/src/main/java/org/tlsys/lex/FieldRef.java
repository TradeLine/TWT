package org.tlsys.lex;

import org.tlsys.lex.members.TField;
import org.tlsys.lex.members.VClass;

public interface FieldRef extends TExpression {
    public TField getField();

    public TExpression getScope();

    @Override
    default VClass getResult() {
        return getField().getType();
    }
}
