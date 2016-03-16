package org.tlsys.twt.rt.java.io;


import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@ClassName("java.io.PrintStream")
public class TPrintStream {
    public TPrintStream(String file) {
        //
    }
    public void println(String text) {
        Script.code("console.info(",text,")");
    }
}
