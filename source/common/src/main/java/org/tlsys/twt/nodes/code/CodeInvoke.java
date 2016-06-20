package org.tlsys.twt.nodes.code;

import org.tlsys.twt.nodes.ClassReferance;
import org.tlsys.twt.nodes.MethodReferance;

public class CodeInvoke extends Value {

    private final MethodReferance method;
    private final CodeNode[] arguments;
    private final Value self;
    private final Type type;

    public CodeInvoke(MethodReferance method, CodeNode[] arguments, Value self, Type type) {
        this.method = method;
        this.arguments = arguments;
        this.self = self;
        this.type = type;
    }

    @Override
    public void accept(CodeVisiter visiter) {
        visiter.visit(this);
    }

    @Override
    public ClassReferance getResultType() {
        throw new RuntimeException("Not supported yet!");
    }

    public enum Type {
        DYNAMIC, SPECIAL
    }
}
