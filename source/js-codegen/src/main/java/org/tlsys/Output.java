package org.tlsys;

import java.io.IOException;

public class Output implements Appendable {

    private StringBuilder builder = new StringBuilder();

    @Override
    public Appendable append(CharSequence csq) throws IOException {
        return builder.append(csq);
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        return builder.append(csq, start, end);
    }

    @Override
    public Appendable append(char c) throws IOException {
        return builder.append(c);
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
