package org.tlsys.twt.nodes.code;

import org.tlsys.twt.nodes.ClassReferance;

public class PrimitiveCastNode extends Value {
    private final ClassReferance type;
    private final Value value;

    public PrimitiveCastNode(ClassReferance type, Value value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public ClassReferance getResultType() {
        return type;
    }

    @Override
    public void accept(CodeVisiter visiter) {
        visiter.visit(this);
    }
}
