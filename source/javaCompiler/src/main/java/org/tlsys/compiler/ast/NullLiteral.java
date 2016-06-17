package org.tlsys.compiler.ast;

import org.tlsys.compiler.generators.AbstractVisitor;
import org.tlsys.compiler.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class NullLiteral extends Expression {

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }
}
