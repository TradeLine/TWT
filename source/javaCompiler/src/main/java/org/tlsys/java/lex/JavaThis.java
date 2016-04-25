package org.tlsys.java.lex;

import org.tlsys.twt.TNode;
import org.tlsys.twt.expressions.This;
import org.tlsys.twt.members.VClass;

public class JavaThis implements This {
    private static final long serialVersionUID = 6293721661184749165L;
    private final TNode parent;
    private final VClass clazz;

    public JavaThis(VClass clazz, TNode parent) {
        this.parent = parent;
        this.clazz = clazz;
    }

    @Override
    public VClass getClassRef() {
        return clazz;
    }

    @Override
    public TNode getParent() {
        return parent;
    }
}
