package org.tlsys.compiler.ast;

import org.tlsys.compiler.generators.AbstractVisitor;
import org.tlsys.compiler.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class ConditionalExpression extends Expression {

    private Expression conditionExpression = null;

    private Expression thenExpression = null;

    private Expression elseExpression = null;

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

    public Expression getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(Expression theConditionExpression) {
        widen(theConditionExpression);
        conditionExpression = theConditionExpression;
    }

    public Expression getElseExpression() {
        return elseExpression;
    }

    public void setElseExpression(Expression theElseExpression) {
        widen(theElseExpression);
        elseExpression = theElseExpression;
    }

    public Expression getThenExpression() {
        return thenExpression;
    }

    public void setThenExpression(Expression theThenExpression) {
        widen(theThenExpression);
        thenExpression = theThenExpression;
    }

}