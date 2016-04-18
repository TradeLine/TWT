package org.tlsys.lex;

public interface TBlock extends TStatement {
    public TStatement getStatement(int index);

    public int getStatementCount();
}
