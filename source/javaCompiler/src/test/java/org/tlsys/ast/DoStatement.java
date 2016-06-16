package org.tlsys.ast;

import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class DoStatement extends LoopStatement {

    public DoStatement() {
        super();
    }

    public DoStatement(int theBeginIndex) {
        super(theBeginIndex);
    }

    public DoStatement(int theBeginIndex, int theEndIndex) {
        super(theBeginIndex, theEndIndex);
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }
}
