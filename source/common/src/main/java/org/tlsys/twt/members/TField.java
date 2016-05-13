package org.tlsys.twt.members;

import org.tlsys.twt.expressions.TExpression;
import org.tlsys.twt.links.FieldVal;

public interface TField extends NamedVariabel, VMember {
    @Override
    public VClass getParent();

    public TExpression getInitValue();

    public default FieldVal asRef() {
        return new FieldVal(getParent().asRef(), getName());
    }
}
