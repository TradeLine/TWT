package org.tlsys.twt.expressions;

import org.tlsys.twt.ConstructorVal;
import org.tlsys.twt.TNode;
import org.tlsys.twt.links.ClassVal;

public final class NewObject extends TExpression {

    private static final long serialVersionUID = -8849661153972212434L;
    private final TNode parent;
    private final ConstructorVal constructorRef;
    private final TExpression[] arguments;

    public NewObject(TNode parent, ConstructorVal constructorRef, TExpression[] arguments) {
        this.parent = parent;
        this.constructorRef = constructorRef;
        this.arguments = arguments;
    }

    public TExpression[] getArguments() {
        return arguments;
    }

    public ConstructorVal getConstructor() {
        return constructorRef;
    }


    @Override
    public ClassVal getResult() {
        return getConstructor().getParent();
    }

    @Override
    public TNode getParent() {
        return parent;
    }
}
