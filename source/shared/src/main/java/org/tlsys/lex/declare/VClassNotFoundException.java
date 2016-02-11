package org.tlsys.lex.declare;

import org.tlsys.twt.CompileException;

public class VClassNotFoundException extends CompileException {
    private String name;

    public VClassNotFoundException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return name;
    }
}
