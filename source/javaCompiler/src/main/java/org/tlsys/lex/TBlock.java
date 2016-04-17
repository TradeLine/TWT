package org.tlsys.lex;

import java.util.List;

public interface TBlock extends TStatement {
    public List<TStatement> getStatements();
}
