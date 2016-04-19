package org.tlsys.java.lex;

import org.tlsys.lex.LocalVar;
import org.tlsys.lex.TNode;
import org.tlsys.lex.TVar;
import org.tlsys.lex.VarRef;
import org.tlsys.lex.members.VClass;

import java.util.Objects;

/**
 * Created by subochev on 19.04.16.
 */
public class JavaVarRef implements VarRef {
    private final LocalVar var;
    private final TNode parent;

    public JavaVarRef(LocalVar var, TNode parent) {
        this.var = Objects.requireNonNull(var);
        this.parent = parent;
    }

    @Override
    public LocalVar getVar() {
        return var;
    }

    @Override
    public TNode getParent() {
        return parent;
    }
}
