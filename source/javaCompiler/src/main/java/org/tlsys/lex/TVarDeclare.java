package org.tlsys.lex;

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
