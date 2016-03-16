package org.tlsys.twt;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class StringOutputStream {
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private PrintStream stream;
    public StringOutputStream() {
        stream = new PrintStream(out);
    }

    public PrintStream getStream() {
        return stream;
    }

    @Override
    public String toString() {
        return new String(out.toByteArray());
    }
}
