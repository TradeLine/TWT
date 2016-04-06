package org.tlsys.lex.declare;

import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.CompileException;

public class VClassNotFoundException extends CompileException {
    private static final long serialVersionUID = -6398611901367607623L;
    private String name;

    public VClassNotFoundException(String name, SourcePoint point) {
        super(point);
        this.name = name;
    }

    @Override
    public String getMessage() {
        return name;
    }
}
