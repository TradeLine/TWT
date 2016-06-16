package org.tlsys.ast;

import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class ContinueStatement extends LabeledJump {

    public ContinueStatement(Block block) {
        super(block);
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

}