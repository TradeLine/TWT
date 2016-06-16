package org.tlsys.ast;

import org.apache.bcel.generic.Type;
import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

public class ThisExpression extends VariableBinding {

    private static VariableDeclaration vd;

    static {
        vd = new VariableDeclaration(VariableDeclaration.NON_LOCAL);
        vd.setName("this");
        vd.setType(Type.OBJECT);
    }

    public ThisExpression() {
        super(vd);
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }
}
