package org.tlsys.ast;

import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class FieldRead extends FieldAccess {

    public FieldRead() {
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

}
