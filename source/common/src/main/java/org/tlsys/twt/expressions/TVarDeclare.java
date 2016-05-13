package org.tlsys.twt.expressions;

import org.tlsys.twt.TNode;
import org.tlsys.twt.links.ClassVal;
import org.tlsys.twt.members.TVar;

import java.util.Objects;

/**
 * Delcate var interface
 */
public final class TVarDeclare extends TStaticExpression {

    private static final long serialVersionUID = -6595448166459155960L;
    private final TVar[] vars;

    public TVarDeclare(TNode parent, TVar[] vars) {
        super(parent);
        this.vars = Objects.requireNonNull(vars);
        if (vars.length <= 0)
            throw new IllegalArgumentException("List of vars must have some value");
    }

    /**
     * Must return decalring variable
     *
     * @return relcaring variable
     */
    public TVar[] getVars() {
        return vars;
    }

    @Override
    public ClassVal getResult() {
        return getVars()[0].getType();
    }
}
