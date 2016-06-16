package org.tlsys.ast;

import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class SynchronizedBlock extends Block {
    public Expression monitor;

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }
}
