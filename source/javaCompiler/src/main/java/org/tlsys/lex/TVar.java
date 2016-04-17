package org.tlsys.lex;

import org.tlsys.lex.members.VClass;

public interface TVar extends TNode, Named {
    public VClass getType();
    /**
     * Init value for declaring variable
     * @return Retuns init value for declaring variable
     */
    public TExpression getInitValue();
}
