package org.tlsys.ast;

import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class PrefixExpression extends PStarExpression {

    static public Operator NOT = new Operator("!");

    static public Operator MINUS = new Operator("-");

    static public Operator PLUS = new Operator("+");

    static public Operator COMPLEMENT = new Operator("~");

    public PrefixExpression() {
        super();
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }
}
