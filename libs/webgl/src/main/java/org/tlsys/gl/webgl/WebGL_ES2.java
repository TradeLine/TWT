package org.tlsys.gl.webgl;

import org.tlsys.gl.*;
import org.tlsys.twt.ArrayTools;
import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class WebGL_ES2 extends WebGL {
    @Override
    protected Object createContext() {
        return Script.code(this, ".getContext('webgl')");
    }

    @Override
    public GLBuffer createBuffer() {
        return new WebGLBuffer(Script.code(getCtx(), ".createBuffer()"), this);
    }

    @Override
    public void bindBuffer(int target, GLBuffer buffer) {
        WebGLBuffer b = (WebGLBuffer) buffer;
        Script.code(getCtx(), ".bindBuffer(", CastUtil.toObject(target), ",", b.getJSObject(), ")");
    }

    @Override
    public void bufferData(int target, float[] data, int usage) {
        Script.code(getCtx(), ".bufferData(", CastUtil.toObject(target), ",", "new Float32Array(", ArrayTools.asJSArray(data), "),", CastUtil.toObject(usage), ")");
    }

    @Override
    public void bufferData(int target, int[] data, int usage) {
        Script.code(getCtx(), ".bufferData(", CastUtil.toObject(target), ",", "new Uint16Array(", ArrayTools.asJSArray(data), "),", CastUtil.toObject(usage), ")");
    }

    @Override
    public GLShader createShader(int type) {
        return new WebGLShader(Script.code(getCtx(), ".createShader(", CastUtil.toObject(type), ")"), this);
    }

    @Override
    public GLProgram createProgram() {
        return new WebGLProgram(this, Script.code(getCtx(), ".createProgram()"));
    }

    @Override
    public void vertexAttribPointer(long index, long size, long type, boolean normalized, long stride, long offset) {
        Script.code(getCtx(), ".vertexAttribPointer(", CastUtil.toObject(index), ",",
                CastUtil.toObject(size), ",", CastUtil.toObject(type),
                ",", CastUtil.toObject(normalized), ",", CastUtil.toObject(stride), ",", CastUtil.toObject(offset), ")");
    }

    @Override
    public void enableVertexAttribArray(long index) {
        Script.code(getCtx(), ".enableVertexAttribArray(", CastUtil.toObject(index), ")");
    }

    @Override
    public void useProgram(GLProgram program) {
        WebGLProgram p = (WebGLProgram) program;
        Script.code(getCtx(), ".useProgram(", p.getJSObject(), ")");
    }

    @Override
    public void enable(long cap) {
        Script.code(getCtx(), ".enable(", CastUtil.toObject(cap), ")");
    }

    @Override
    public void depthFunc(long func) {
        Script.code(getCtx(), ".depthFunc(", CastUtil.toObject(func), ")");
    }

    @Override
    public void clearColor(float red, float green, float blue, float alpha) {
        Script.code(getCtx(), ".clearColor(", CastUtil.toObject(red), ",", CastUtil.toObject(green), ",",
                CastUtil.toObject(blue), ",", CastUtil.toObject(alpha), ")");
    }

    @Override
    public void clearDepth(float depth) {
        Script.code(getCtx(), ".clearDepth(", CastUtil.toObject(depth), ")");
    }

    @Override
    public void viewport(long x, long y, long width, long height) {
        Script.code(getCtx(), ".viewport(", CastUtil.toObject(x), ",", CastUtil.toObject(y), ",",
                CastUtil.toObject(width), ",", CastUtil.toObject(height), ")");
    }

    @Override
    public void clear(long mask) {
        Script.code(getCtx(), ".clear(", CastUtil.toObject(mask), ")");
    }

    @Override
    public void drawElements(long mode, long count, long type, long offset) {
        Script.code(getCtx(), ".drawElements(", CastUtil.toObject(mode), ",",
                CastUtil.toObject(count), ",",
                CastUtil.toObject(type), ",",
                CastUtil.toObject(offset), ")");
    }

    @Override
    public GLTexture createTexture() {
        return new WebGLTexture(Script.code(getCtx(), ".createTexture()"), this);
    }

    @Override
    public void bindTexture(long target, GLTexture texture) {
        if (texture == null) {
            Script.code(getCtx(), ".bindTexture(", CastUtil.toObject(target), ",null)");
        } else {
            WebGLTexture t = (WebGLTexture) texture;
            Script.code(getCtx(), ".bindTexture(", CastUtil.toObject(target), ",", t.getJSObject(), ")");
        }
    }

    @Override
    public void uniformMatrix4fv(GLUniformLocation location, boolean transpose, double[] value) {
        WebGLUniformLocation l = (WebGLUniformLocation) location;
        Script.code(getCtx(), ".uniformMatrix4fv(", l.getJs(), ",", CastUtil.toObject(transpose),
                ",new Float32Array(", ArrayTools.asJSArray(value), "))");
    }
}
