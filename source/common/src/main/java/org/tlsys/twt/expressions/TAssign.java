package org.tlsys.twt.expressions;

import org.tlsys.twt.TNode;
import org.tlsys.twt.links.ClassVal;

import java.util.Objects;

public final class TAssign extends TStaticExpression {
    private static final long serialVersionUID = 689134245522611054L;

    private TExpression target;
    private TExpression value;

    public TAssign(TNode parent) {
        super(parent);
    }

    public TExpression getTarget() {
        return target;
    }

    public void setTarget(TExpression target) {
        if (Objects.requireNonNull(target).getParent() != this)
            throw new IllegalArgumentException("Target must have this object as parent");
        this.target = target;
    }

    public TExpression getValue() {
        return value;
    }

    public void setValue(TExpression value) {
        if (Objects.requireNonNull(value).getParent() != this)
            throw new IllegalArgumentException("Value must have this object as parent");
        this.value = value;
    }

    @Override
    public ClassVal getResult() {
        return getTarget().getResult();
    }
}
