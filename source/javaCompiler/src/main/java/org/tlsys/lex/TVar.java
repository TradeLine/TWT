package org.tlsys.lex;

import org.tlsys.lex.members.NamedVariabel;
import org.tlsys.lex.members.VClass;

public interface TVar extends LocalVar {
    /**
     * Init value for declaring variable
     * @return Retuns init value for declaring variable
     */
    public TExpression getInitValue();
}
