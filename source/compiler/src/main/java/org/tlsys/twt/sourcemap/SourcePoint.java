package org.tlsys.twt.sourcemap;

/**
 * Класс точки в исходном файле
 */
public class SourcePoint {
    private final int row;
    private final int column;
    private final SourceFile sourceFile;

    public SourcePoint(int row, int column, SourceFile sourceFile) {
        this.row = row;
        this.column = column;
        this.sourceFile = sourceFile;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public SourceFile getSourceFile() {
        return sourceFile;
    }
}
