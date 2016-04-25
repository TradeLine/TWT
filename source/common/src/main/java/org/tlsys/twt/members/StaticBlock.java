package org.tlsys.twt.members;

import org.tlsys.twt.statement.TBlock;

public interface StaticBlock extends TBlock {
    @Override
    public VClass getParent();
}
