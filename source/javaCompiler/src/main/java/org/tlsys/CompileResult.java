package org.tlsys;

import org.tlsys.twt.nodes.TClass;

public class CompileResult {
    private final TClass result;
    private final TClass[] added;

    public CompileResult(TClass result, TClass[] added) {
        this.result = result;
        this.added = added;
    }

    public TClass getResult() {
        return result;
    }

    public TClass[] getAdded() {
        return added;
    }
}
