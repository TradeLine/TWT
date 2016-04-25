package org.tlsys.java.lex;

import org.tlsys.twt.members.VMember;

public abstract class JavaMember implements VMember {

    private static final long serialVersionUID = 195508407642810070L;
    private final VMember parent;

    public JavaMember(VMember parent) {
        this.parent = parent;
    }

    @Override
    public VMember getParent() {
        return parent;
    }
}
