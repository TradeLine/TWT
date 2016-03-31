package org.tlsys;

import org.tlsys.sourcemap.SourceMap;
import org.tlsys.sourcemap.SourcePoint;

import java.io.IOException;
import java.util.ArrayList;

public class Outbuffer implements Appendable {
    private int current;
    private final Appendable out;

    public Outbuffer(Appendable out) {
        this.out = out;
    }

    @Override
    public Outbuffer append(CharSequence csq) {
        try {
            out.append(csq);
            current += csq.length();
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Outbuffer append(CharSequence csq, int start, int end) {
        try {
            out.append(csq, start, end);
            if (end >= csq.length())
                end = csq.length() - 1;
            if (start < 0)
                start = 0;

            current += end - start + 1;

            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Outbuffer append(char c) {
        try {
            out.append(c);
            ++current;
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getCurrent() {
        return current;
    }

    private ArrayList<SourceMap.Record> records = new ArrayList<>();

    public Outbuffer add(CharSequence csq, SourcePoint point, String name) {
        records.add(new SourceMap.Record(point.getSourceFile(), point, current, name));
        append(csq);
        return this;
    }

    public Outbuffer add(CharSequence csq, SourcePoint point) {
        return add(csq, point, null);
    }
}
