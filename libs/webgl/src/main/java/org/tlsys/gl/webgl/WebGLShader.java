package org.tlsys.gl.webgl;

import org.tlsys.gl.GLShader;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class WebGLShader implements GLShader {
    private final Object s;
    private final WebGL gl;

    public WebGLShader(Object s, WebGL gl) {
        this.s = s;
        this.gl = gl;
    }

    public Object getJSObject() {
        return s;
    }

    @Override
    public void setSource(String source) {
        Script.code(gl.getCtx(), ".shaderSource(", s, ",", source, ")");
    }

    @Override
    public void compile() {
        Script.code(gl.getCtx(), ".compileShader(", s, ")");
    }
}
