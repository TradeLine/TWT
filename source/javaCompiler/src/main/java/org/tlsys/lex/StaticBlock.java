package org.tlsys.lex;

import org.tlsys.lex.members.VClass;

public interface StaticBlock extends TBlock {
    @Override
    VClass getParent();
}
