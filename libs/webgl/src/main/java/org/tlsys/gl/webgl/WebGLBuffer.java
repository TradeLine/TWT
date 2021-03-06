package org.tlsys.gl.webgl;

import org.tlsys.gl.GLBuffer;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class WebGLBuffer implements GLBuffer {
    private final Object buf;
    private final WebGL gl;
    private boolean deleted;

    public WebGLBuffer(Object buf, WebGL gl) {
        this.buf = buf;
        this.gl = gl;
    }

    public Object getJSObject() {
        return buf;
    }

    @Override
    public void delete() {
        if (isDeleted())
            throw new IllegalStateException("Buffer already deleted");
        Script.code(gl.getCtx(), ".deleteBuffer(", buf, ")");
        deleted = true;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }
}
