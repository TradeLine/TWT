package org.tlsys.twt.members;

import org.tlsys.twt.TNode;

public interface NamedVariabel extends Named, TNode {
    public VClass getType();
}
