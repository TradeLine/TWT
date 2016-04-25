package org.tlsys.twt.statement;

public interface TBlock extends TStatement {
    public TStatement getStatement(int index);

    public int getStatementCount();
}
