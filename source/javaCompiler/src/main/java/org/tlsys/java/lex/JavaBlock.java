package org.tlsys.java.lex;

import org.tlsys.lex.TBlock;
import org.tlsys.lex.TNode;
import org.tlsys.lex.TStatement;

import java.util.ArrayList;
import java.util.List;

public class JavaBlock implements TBlock {

    private static final long serialVersionUID = 4609415030959114525L;
    private final List<TStatement> statements = new ArrayList<>();
    private final TNode parent;

    public JavaBlock(TNode parent) {
        this.parent = parent;
    }

    public JavaBlock add(TStatement statement) {
        statements.add(statement);
        return this;
    }

    @Override
    public TNode getParent() {
        return parent;
    }

    @Override
    public TStatement getStatement(int index) {
        return statements.get(index);
    }

    @Override
    public int getStatementCount() {
        return statements.size();
    }
}
