package org.tlsys.twt.members;

import org.tlsys.twt.ExecuteVal;

import java.util.List;

public interface VExecute extends VMember {
    @Override
    public VClass getParent();

    public List<TArgument> getArguments();

    public ExecuteVal asRef();
}
