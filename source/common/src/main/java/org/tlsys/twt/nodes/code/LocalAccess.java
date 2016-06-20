package org.tlsys.twt.nodes.code;

import org.tlsys.twt.nodes.ClassReferance;

public class LocalAccess extends Value {
    private final ClassReferance type;

    public LocalAccess(ClassReferance type) {
        this.type = type;
    }

    @Override
    public ClassReferance getResultType() {
        return type;
    }

    @Override
    public void accept(CodeVisiter visiter) {
        visiter.visit(this);
    }
}
