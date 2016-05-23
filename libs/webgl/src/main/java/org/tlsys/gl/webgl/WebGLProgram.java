package org.tlsys.gl.webgl;

import org.tlsys.gl.*;
import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class WebGLProgram implements GLProgram {
    private final Object p;
    private final WebGL gl;
    private boolean deleted = false;

    WebGLProgram(WebGL gl, Object p) {
        this.gl = gl;
        this.p = p;
    }

    public Object getJSObject() {
        return p;
    }

    @Override
    public void attach(GLShader shader) {
        WebGLShader s = (WebGLShader) shader;
        Script.code(gl.getCtx(), ".attachShader(", p, ",", s.getJSObject(), ")");
    }

    @Override
    public void detach(GLShader shader) {
        WebGLShader s = (WebGLShader)shader;
        Script.code(gl.getCtx(),".detachShader(",p,",",s.getJSObject(),")");
    }

    @Override
    public void link() {
        Script.code(gl.getCtx(), ".linkProgram(", p, ")");
    }

    @Override
    public GLUniformLocation getUniformLocation(String name) {
        return new WebGLUniformLocation(Script.code(gl.getCtx(), ".getUniformLocation(", p, ",", name, ")"));
    }

    @Override
    public long getAttribLocation(String name) {
        return CastUtil.toLong(Script.code(gl.getCtx(), ".getAttribLocation(", p, ",", name, ")"));
    }

    @Override
    public void delete() {
        if (isDeleted())
            throw new IllegalStateException("Program already was deleted");
        Script.code(gl.getCtx(),".deleteProgram(",p,")");
        deleted = true;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }
}
