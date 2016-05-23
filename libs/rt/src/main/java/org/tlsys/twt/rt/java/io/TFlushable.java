package org.tlsys.twt.rt.java.io;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

import java.io.Flushable;
import java.io.IOException;

@JSClass
@ReplaceClass(Flushable.class)
public interface TFlushable {
    void flush() throws IOException;
}
