package org.tlsys.twt.nodes.code;

import org.tlsys.twt.nodes.ClassReferance;

public class ClassAccess extends Value {
    private final ClassReferance referance;

    public ClassAccess(ClassReferance referance) {
        this.referance = referance;
    }

    @Override
    public ClassReferance getResultType() {
        return referance;
    }

    @Override
    public void accept(CodeVisiter visiter) {
        visiter.visit(this);
    }
}
