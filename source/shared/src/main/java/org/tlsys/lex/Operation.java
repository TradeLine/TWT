package org.tlsys.lex;

import org.tlsys.ReplaceVisiter;
import org.tlsys.twt.CompileException;

import java.io.Serializable;

public abstract class Operation implements Using, Context, Serializable {
    private static final long serialVersionUID = -2199466331939651448L;

    public void visit(ReplaceVisiter replaceControl) {
        //
    }

    public boolean accept(OperationVisiter visiter) throws CompileException {
        return visiter.visit(this);
    }
}
