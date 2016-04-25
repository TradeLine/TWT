package org.tlsys.twt.expressions;

import org.tlsys.twt.members.TField;
import org.tlsys.twt.members.VClass;

import java.util.List;

public interface AnntationItem {
    public VClass getType();

    public List<Value> getValues();

    public interface Value {
        TField getField();

        TExpression getValue();
    }
}
