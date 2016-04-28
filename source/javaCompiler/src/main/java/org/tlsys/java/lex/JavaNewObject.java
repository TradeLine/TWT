package org.tlsys.java.lex;

import org.tlsys.twt.TNode;
import org.tlsys.twt.expressions.NewObject;
import org.tlsys.twt.expressions.TExpression;
import org.tlsys.twt.members.TArgument;
import org.tlsys.twt.members.TConstructor;

import java.util.HashMap;
import java.util.Map;

public class JavaNewObject implements NewObject {

    private static final long serialVersionUID = -1841337438560541244L;
    private final TNode parent;
    private HashMap<TArgument, TExpression> args = new HashMap<>();
    private TConstructor constructor;

    public JavaNewObject(TNode parent) {
        this.parent = parent;
    }

    public JavaNewObject arg(TArgument arg, TExpression expression) {
        args.put(arg, expression);
        return this;
    }

    @Override
    public Map<TArgument, TExpression> getArguments() {
        return args;
    }

    @Override
    public TConstructor getConstructor() {
        return constructor;
    }

    public void setConstructor(TConstructor constructor) {
        this.constructor = constructor;
    }

    @Override
    public TNode getParent() {
        return parent;
    }
}
