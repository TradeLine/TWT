package org.tlsys.twt.nodes.code;

public class CodeBlock extends CodeNode {
    public CodeBlock(CodeNode[] childs) {
        this.childs = childs;
    }

    @Override
    public void accept(CodeVisiter visiter) {
        visiter.visit(this);
    }

    private final CodeNode[] childs;

    public CodeNode[] getChilds() {
        return childs;
    }
}
