package org.tlsys.twt.rt.java.io;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

import java.io.Closeable;
import java.io.Flushable;

@JSClass
@ClassName("java.io.OutputStream")
@ReplaceClass(java.io.OutputStream.class)
public abstract class TOutputStream implements TCloseable, TFlushable {
}
