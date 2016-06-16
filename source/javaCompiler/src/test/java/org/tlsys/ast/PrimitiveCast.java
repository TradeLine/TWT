package org.tlsys.ast;

import org.apache.bcel.generic.Type;
import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class PrimitiveCast extends Expression {
    public int castType = 0;

    public Expression expression;

    public PrimitiveCast(int theCastType, Expression expr, Type typeBinding) {
        super();
        type = typeBinding;
        castType = theCastType;
        expression = expr;
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

    public Expression getExpression() {
        return expression;
    }
}
