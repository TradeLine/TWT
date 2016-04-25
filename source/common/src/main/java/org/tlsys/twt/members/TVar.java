package org.tlsys.twt.members;

import org.tlsys.twt.expressions.TExpression;

public interface TVar extends LocalVar {
    /**
     * Init value for declaring variable
     * @return Retuns init value for declaring variable
     */
    public TExpression getInitValue();
}
