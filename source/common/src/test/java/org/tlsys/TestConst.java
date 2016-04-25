package org.tlsys;

import org.tlsys.twt.TNode;
import org.tlsys.twt.expressions.TConst;
import org.tlsys.twt.members.VClass;

public class TestConst implements TConst {
    private static final long serialVersionUID = -4125753351348399884L;

    private final Object value;
    private final TNode parent;
    private final VClass type;

    public TestConst(Object value, VClass type, TNode parent) {
        this.value = value;
        this.parent = parent;
        this.type = type;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public VClass getResult() {
        return type;
    }

    @Override
    public TNode getParent() {
        return parent;
    }
}
