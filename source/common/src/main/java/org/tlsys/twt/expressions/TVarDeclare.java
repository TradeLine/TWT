package org.tlsys.twt.expressions;

import org.tlsys.twt.members.TVar;

import java.util.List;

/**
 * Delcate var interface
 */
public interface TVarDeclare extends TExpression {

    /**
     * Must return decalring variable
     * @return relcaring variable
     */
    public List<TVar> getVars();
}
