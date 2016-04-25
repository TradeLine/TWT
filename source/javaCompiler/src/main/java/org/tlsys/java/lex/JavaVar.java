package org.tlsys.java.lex;

import com.github.javaparser.ast.body.VariableDeclarator;
import org.tlsys.JavaCompiller;
import org.tlsys.twt.TNode;
import org.tlsys.twt.expressions.TExpression;
import org.tlsys.twt.members.TVar;
import org.tlsys.twt.members.VClass;

public class JavaVar implements TVar {

    private static final long serialVersionUID = -3506994485280348414L;
    private final VariableDeclarator declare;
    private final String name;
    private final TNode parent;
    private final TExpression initValue;
    private final VClass type;

    public JavaVar(VariableDeclarator declare, TNode parent, VClass type) {
        this.declare = declare;
        this.parent = parent;
        this.type = type;
        name = declare.getId().getName();
        if (declare.getInit() == null)
            initValue = JavaCompiller.getInitValueFor(type);
        else
            initValue = JavaCompiller.expression(declare.getInit(), parent, getType());
    }

    @Override
    public VClass getType() {
        return type;
    }

    @Override
    public TExpression getInitValue() {
        return initValue;
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
