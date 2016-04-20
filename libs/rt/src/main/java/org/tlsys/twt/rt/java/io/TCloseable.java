package org.tlsys.twt.rt.java.io;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

import java.io.Closeable;
import java.io.IOException;

@JSClass
@ReplaceClass(Closeable.class)
public interface TCloseable {
    void close() throws IOException;
}
