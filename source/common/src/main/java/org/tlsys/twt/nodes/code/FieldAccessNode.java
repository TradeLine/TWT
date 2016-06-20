package org.tlsys.twt.nodes.code;

public abstract class FieldAccessNode extends Value {
    private final Value self;

    public FieldAccessNode(Value self) {
        this.self = self;
    }

    public Value getSelf() {
        return self;
    }

    @Override
    public void accept(CodeVisiter visiter) {
        visiter.visit(this);
    }
}
