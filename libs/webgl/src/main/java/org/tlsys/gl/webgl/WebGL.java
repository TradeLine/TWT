package org.tlsys.gl.webgl;

import org.tlsys.gl.GL;
import org.tlsys.twt.annotations.DomNode;
import org.tlsys.twt.annotations.JSClass;

@DomNode("canvas")
@JSClass
public abstract class WebGL implements GL {
    protected abstract Object createContext();
    private Object ctx = null;

    Object getCtx() {
        if (ctx == null)
            ctx = createContext();
        return ctx;
    }
}
