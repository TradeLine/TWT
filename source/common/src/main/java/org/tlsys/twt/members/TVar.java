package org.tlsys.twt.members;

import org.tlsys.twt.TNode;
import org.tlsys.twt.expressions.TExpression;
import org.tlsys.twt.links.ClassVal;

public class TVar implements LocalVar {
    private static final long serialVersionUID = 5759495221034261759L;
    private final String name;
    private final TNode parent;
    private final ClassVal type;
    private TExpression init;

    public TVar(String name, TNode parent, ClassVal type) {
        this.name = name;
        this.parent = parent;
        this.type = type;
    }

    /**
     * Init value for declaring variable
     * @return Retuns init value for declaring variable
     */
    public TExpression getInitValue() {
        return init;
    }

    public void setInitValue(TExpression init) {
        this.init = init;
    }

    @Override
    public ClassVal getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TNode getParent() {
        return parent;
    }
}
