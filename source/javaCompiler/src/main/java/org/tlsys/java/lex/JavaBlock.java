package org.tlsys.java.lex;

import org.tlsys.lex.TBlock;
import org.tlsys.lex.TNode;
import org.tlsys.lex.TStatement;

import java.util.ArrayList;
import java.util.List;

public class JavaBlock implements TBlock {

    private final List<TStatement> statements = new ArrayList<>();

    public JavaBlock add(TStatement statement) {
        statements.add(statement);
        return this;
    }

    @Override
    public List<TStatement> getStatements() {
        return statements;
    }

    @Override
    public TNode getParent() {
        return null;
    }
}
