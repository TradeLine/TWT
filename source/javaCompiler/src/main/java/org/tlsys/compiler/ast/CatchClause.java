package org.tlsys.compiler.ast;

import org.tlsys.compiler.generators.AbstractVisitor;
import org.tlsys.compiler.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class CatchClause extends Block {

    private VariableDeclaration exception;

    public CatchClause(int theBeginIndex) {
        super(theBeginIndex);
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

    public VariableDeclaration getException() {
        return exception;
    }

    public void setException(VariableDeclaration theException) {
        exception = theException;
    }
}
