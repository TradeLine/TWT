package org.tlsys.java.lex;

import org.tlsys.lex.TNode;
import org.tlsys.lex.This;
import org.tlsys.lex.members.VClass;

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
