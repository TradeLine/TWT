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
        sb.append(point.getSourceFile().getName()).append(":").append(point.getColumn()).append(": ").append(super.getMessage()).append("\n");
        /*
        int start = point.getSourceFile().getIndex(point.getColumn(), 0);
        int end = point.getSourceFile().getData().indexOf('\n', start) - 1;
        if (end == -2)
            end = point.getSourceFile().getData().length()-1;
        //int end = point.getSourceFile().getIndex(point.getColumn() + 1, 0) - 1;
        String line = point.getSourceFile().getData().substring(start, end);
        sb.append(line).append("\n");
        for (int i = 0; i < point.getRow(); i++) {
            sb.append(" ");
        }
        sb.append("^");
        */
        return sb.toString();
    }
}
