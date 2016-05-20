package org.tlsys.twt;

import org.tlsys.sourcemap.SourcePoint;

public class CompileException extends Exception {
    private static final long serialVersionUID = -6426029898306335560L;

    private SourcePoint point;

    public CompileException(SourcePoint point) {
        super();
        this.point = point;
    }

    public CompileException(String message, SourcePoint point) {
        super(message);
        this.point = point;
    }

    public CompileException(String message, Throwable cause, SourcePoint point) {
        super(message, cause);
        this.point = point;
    }

    public CompileException(Throwable cause, SourcePoint point) {
        super(cause);
        this.point = point;
    }

    @Override
    public String getMessage() {
        if (point == null)
            return super.getMessage();

        StringBuilder sb = new StringBuilder();
        sb.append(point.getSourceFile().getName()).append(":").append(point.getRow() + 1).append(":").append(point.getColumn() + 1).append(": ").append(super.getMessage());
/*
        sb.append("\n")
        int start = point.getSourceFile().getIndex(point.getRow(), 0);
        int end = point.getSourceFile().getData().indexOf('\n', start) - 1;
        if (end == -2)
            end = point.getSourceFile().getData().length() - 1;

        if (end - start > 50) {
            int newStart = point.getColumn() - 25;
            int newEnd = point.getColumn() + 25;
            start = start > newStart ? start : newStart;
            end = end < newEnd ? end : newEnd;
        }

        //int end = point.getSourceFile().getIndex(point.getColumn() + 1, 0) - 1;
        String line = point.getSourceFile().getData().substring(start, end);
        sb.append("\n").append(line).append("\n");
        for (int i = 0; i < point.getColumn() - start; i++) {
            sb.append(" ");
        }
        sb.append("^");
        */
        return sb.toString();
    }
}
