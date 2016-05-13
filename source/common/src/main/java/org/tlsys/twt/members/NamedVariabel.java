package org.tlsys.twt.members;

import org.tlsys.twt.TNode;
import org.tlsys.twt.links.ClassVal;

public interface NamedVariabel extends Named, TNode {
    public ClassVal getType();
}
