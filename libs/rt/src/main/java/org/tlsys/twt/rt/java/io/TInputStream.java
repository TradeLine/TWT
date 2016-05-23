package org.tlsys.twt.rt.java.io;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

import java.io.Closeable;
import java.io.InputStream;

@JSClass
@ReplaceClass(InputStream.class)
public abstract class TInputStream implements Closeable {
}
