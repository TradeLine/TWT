package org.tlsys.java.lex;

import org.tlsys.lex.StaticRef;
import org.tlsys.lex.TNode;
import org.tlsys.lex.members.VClass;

/**
 * Created by Субочев Антон on 20.04.2016.
 */
public class JavaStaticRef implements StaticRef {
    private static final long serialVersionUID = 6584591303337994320L;
    private final TNode parent;
    private final VClass clazz;

    public JavaStaticRef(VClass clazz, TNode parent) {
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
