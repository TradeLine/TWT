package org.tlsys.ast;

import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

import java.util.List;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class SwitchCase extends Block {

    private List<NumberLiteral> expressions;

    public SwitchCase(int theBeginIndex) {
        super(theBeginIndex);
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

    public List<NumberLiteral> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<NumberLiteral> theExpressions) {
        expressions = theExpressions;
    }

}
