package org.tlsys.gl;

import org.tlsys.twt.annotations.JSClass;

@JSClass
public interface GLTexture {
    public void delete();

    public boolean isDeleted();
}
