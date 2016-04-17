package org.tlsys.java.lex;

import com.github.javaparser.ast.body.VariableDeclarator;
import org.tlsys.JavaCompiller;
import org.tlsys.lex.TExpression;
import org.tlsys.lex.TNode;
import org.tlsys.lex.TVar;
import org.tlsys.lex.members.VClass;

public class JavaVar implements TVar {

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
