package org.tlsys.twt.nodes.code;

import org.tlsys.twt.nodes.ClassReferance;
import org.tlsys.twt.nodes.SimpleClassReferance;

public class ThisNode extends Value {

    private final SimpleClassReferance thisClass;

    public ThisNode(SimpleClassReferance thisClass) {
        this.thisClass = thisClass;
    }

    @Override
    public ClassReferance getResultType() {
        return thisClass;
    }

    @Override
    public void accept(CodeVisiter visiter) {
        visiter.visit(this);
    }
}
