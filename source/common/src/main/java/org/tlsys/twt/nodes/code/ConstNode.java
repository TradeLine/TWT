package org.tlsys.twt.nodes.code;

import org.tlsys.twt.nodes.ClassReferance;

public abstract class ConstNode extends Value {

    @Override
    public void accept(CodeVisiter visiter) {
        visiter.visit(this);
    }
}
