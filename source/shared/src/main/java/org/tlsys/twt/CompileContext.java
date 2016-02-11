package org.tlsys.twt;

public abstract class CompileContext {
    private final Class current;

    public CompileContext(Class current) {
        this.current = current;
    }

    public Class getCurrent() {
        return current;
    }

    public abstract void startBody();
    public abstract void endBody();
    public abstract GenContext getContext();
    public abstract Namer getNamer();
}
