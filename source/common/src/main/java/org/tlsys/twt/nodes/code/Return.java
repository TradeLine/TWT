package org.tlsys.twt.nodes.code;

public class Return extends CodeNode {
    private final CodeNode value;

    public Return(CodeNode value) {
        this.value = value;
    }

    public Return() {
        this(null);
    }

    @Override
    public void accept(CodeVisiter visiter) {
        visiter.visit(this);
    }
}
