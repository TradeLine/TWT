package org.tlsys.twt.nodes.code;

import org.tlsys.twt.FieldReferance;
import org.tlsys.twt.nodes.ClassReferance;

public class FieldReadNode extends FieldAccessNode {
    private final FieldReferance fieldReferance;

    public FieldReadNode(Value self, FieldReferance fieldReferance) {
        super(self);
        this.fieldReferance = fieldReferance;
    }

    @Override
    public ClassReferance getResultType() {
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void accept(CodeVisiter visiter) {
        visiter.visit(this);
    }
}
