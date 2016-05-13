package org.tlsys.twt.expressions;

import org.tlsys.twt.links.ClassVal;
import org.tlsys.twt.members.TField;

import java.util.List;

public interface AnntationItem {
    public ClassVal getType();

    public List<Value> getValues();

    public interface Value {
        TField getField();

        TExpression getValue();
    }
}
