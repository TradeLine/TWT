package org.tlsys.ast;

import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class BooleanLiteral extends Expression {

    public static BooleanLiteral FALSE = new BooleanLiteral(false);

    public static BooleanLiteral TRUE = new BooleanLiteral(true);

    private boolean value;

    public BooleanLiteral(boolean theValue) {
        value = theValue;
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean theValue) {
        value = theValue;
    }
}
