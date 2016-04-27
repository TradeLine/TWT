package org.tlsys;

import java.io.IOException;

public class Output implements Appendable {

    private StringBuilder builder = new StringBuilder();

    @Override
    public Output append(CharSequence csq) {
        builder.append(csq);
        return this;
    }

    @Override
    public Output append(CharSequence csq, int start, int end) {
        builder.append(csq, start, end);
        return this;
    }

    @Override
    public Output append(char c) {
        builder.append(c);
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }


}
