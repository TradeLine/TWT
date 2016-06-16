package org.tlsys.ast;

import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class IfStatement extends Block {
    public IfStatement() {
        super();
    }

    public Expression getExpression() {
        return (Expression) getChildAt(0);
    }

    public void setExpression(Expression expression) {
        widen(expression);
        setChildAt(0, expression);
    }

    public Block getIfBlock() {
        return (Block) getChildAt(1);
    }

    public void setIfBlock(Block block) {
        widen(block);
        setChildAt(1, block);
    }

    public Block getElseBlock() {
        if (getChildCount() < 3)
            return null;
        return (Block) getChildAt(2);
    }

    public void setElseBlock(Block block) {
        widen(block);
        setChildAt(2, block);
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }
}
