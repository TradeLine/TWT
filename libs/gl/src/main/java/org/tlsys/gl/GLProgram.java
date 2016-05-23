package org.tlsys.gl;

import org.tlsys.twt.annotations.JSClass;

@JSClass
public interface GLProgram {
    public void attach(GLShader shader);
    public void detach(GLShader shader);
    public void link();
    GLUniformLocation getUniformLocation(String name);
    public long getAttribLocation(String name);
    public void delete();
    public boolean isDeleted();
}
