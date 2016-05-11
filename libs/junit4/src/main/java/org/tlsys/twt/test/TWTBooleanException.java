package org.tlsys.twt.test;

import org.tlsys.twt.annotations.JSClass;

@JSClass
public class TWTBooleanException extends TWTTestException {
    private static final long serialVersionUID = -6276992484387779132L;
    private final boolean expecteds;
    private final boolean actuals;

    public TWTBooleanException(boolean expecteds, boolean actuals) {
        this.expecteds = expecteds;
        this.actuals = actuals;
    }
}
