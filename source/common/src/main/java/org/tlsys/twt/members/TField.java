package org.tlsys.twt.members;

import org.tlsys.twt.expressions.TExpression;

public interface TField extends NamedVariabel, VMember {
    @Override
    public VClass getParent();

    public TExpression getInitValue();
}
