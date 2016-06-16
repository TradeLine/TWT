package org.tlsys.ast;

import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

import java.util.List;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class ArrayInitializer extends Expression {

    private List<Expression> expressions = new java.util.ArrayList<Expression>();

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

    public List<Expression> getExpressions() {
        return expressions;
    }
}
