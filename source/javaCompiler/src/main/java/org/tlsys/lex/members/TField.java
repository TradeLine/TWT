package org.tlsys.lex.members;

import org.tlsys.lex.TExpression;

public interface TField extends NamedVariabel, VMember {
    @Override
    public VClass getParent();

    public TExpression getInitValue();
}
