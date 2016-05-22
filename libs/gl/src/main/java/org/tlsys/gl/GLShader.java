package org.tlsys.gl;

import org.tlsys.twt.annotations.JSClass;

@JSClass
public interface GLShader {
    public void setSource(String source);
    public void compile();
}
