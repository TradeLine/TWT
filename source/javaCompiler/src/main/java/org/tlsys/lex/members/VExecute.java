package org.tlsys.lex.members;

import java.util.List;

public interface VExecute extends VMember {
    @Override
    public VClass getParent();

    public List<TArgument> getArguments();
}
