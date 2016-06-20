package org.tlsys.twt.nodes.code;

import org.tlsys.twt.nodes.ClassReferance;

public class ClassNew extends Value {
    private final ClassReferance classReferance;

    public ClassNew(ClassReferance classReferance) {
        this.classReferance = classReferance;
    }

    @Override
    public ClassReferance getResultType() {
        return classReferance;
    }

    @Override
    public void accept(CodeVisiter visiter) {
        visiter.visit(this);
    }
}
