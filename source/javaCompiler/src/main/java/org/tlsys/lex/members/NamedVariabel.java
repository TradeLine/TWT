package org.tlsys.lex.members;

import org.tlsys.lex.Named;
import org.tlsys.lex.TNode;

public interface NamedVariabel extends Named, TNode {
    public VClass getType();
}
