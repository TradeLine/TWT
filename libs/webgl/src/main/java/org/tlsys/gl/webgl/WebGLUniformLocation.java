package org.tlsys.gl.webgl;

import org.tlsys.gl.GLUniformLocation;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class WebGLUniformLocation implements GLUniformLocation {
    private final Object js;

    public WebGLUniformLocation(Object js) {
        this.js = js;
    }

    public Object getJs() {
        return js;
    }
}
