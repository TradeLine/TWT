package org.tlsys.gl.webgl;

import org.tlsys.gl.GLTexture;
import org.tlsys.twt.Script;

public class WebGLTexture implements GLTexture {
    private final Object t;
    private final WebGL gl;

    private boolean removed;

    WebGLTexture(Object t, WebGL gl) {
        this.t = t;
        this.gl = gl;
    }

    @Override
    public void delete() {
        if (removed)
            throw new IllegalStateException("Texture already deleted");
        Script.code(gl.getCtx(),".deleteTexture(",t,")");
        removed = true;
    }

    @Override
    public boolean isDeleted() {
        return removed;
    }
}
