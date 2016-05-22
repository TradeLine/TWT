package org.tlsys.gl.webgl;

import org.tlsys.gl.GLBuffer;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class WebGLBuffer implements GLBuffer {
    private final Object buf;
    private final WebGL gl;

    public WebGLBuffer(Object buf, WebGL gl) {
        this.buf = buf;
        this.gl = gl;
    }

    public Object getJSObject() {
        return buf;
    }
}
