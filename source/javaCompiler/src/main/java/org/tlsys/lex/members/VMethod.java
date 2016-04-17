package org.tlsys.lex.members;

import org.tlsys.lex.Named;

public interface VMethod extends VExecute, Named {
    public VClass getResult();
}
