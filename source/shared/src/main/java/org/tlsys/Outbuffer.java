package org.tlsys;

import org.tlsys.sourcemap.SourceMap;
import org.tlsys.sourcemap.SourcePoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Outbuffer implements Appendable {
    private final Appendable out;
    private final LinkedList<HoldState> holdStates = new LinkedList<>();
    private int current;
    private ArrayList<SourceMap.Record> records = new ArrayList<>();

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

    public void pushHold(SourcePoint point) {
        if (point == null)
            holdStates.addFirst(null);
        else
            holdStates.addFirst(new HoldState(point.getRow(), point.getColumn()));
    }

    private HoldState peek() {
        if (holdStates.isEmpty())
            return null;
        return holdStates.getLast();
    }

    public void popHold() {
        holdStates.removeFirst();
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

    public Outbuffer add(CharSequence csq, SourcePoint point, String name) {
        return add(csq, point, getCurrent(), name);
    }

    public Outbuffer add(CharSequence csq, SourcePoint point, int pos, String name) {
        if (point != null) {
            if (peek() != null) {
                records.add(new SourceMap.Record(point.getSourceFile(), point.getSourceFile().getPoint(peek().row, peek().column), current, name));
            } else
                records.add(new SourceMap.Record(point.getSourceFile(), point, current, name));
        }
        append(csq);
        return this;
    }

    public Outbuffer add(CharSequence csq, SourcePoint point) {
        return add(csq, point, getCurrent(), null);
    }

    public Outbuffer add(CharSequence csq, SourcePoint point, int pos) {
        return add(csq, point, pos, null);
    }

    public List<SourceMap.Record> getRecords() {
        return records;
    }

    private class HoldState {
        private final int row;
        private final int column;

        public HoldState(int row, int column) {
            this.row = row;
            this.column = column;
        }
    }
}
