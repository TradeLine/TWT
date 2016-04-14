package org.tlsys.sourcemap;

import java.io.Serializable;

/**
 * Класс точки в исходном файле
 */
public class SourcePoint implements Serializable {
    private static final long serialVersionUID = 3610709938007437817L;
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

    @Override
    public String toString() {
        return "SourcePoint{" + (row + 1) + ":" + (column + 1) + " in " + sourceFile.getName() + '}';
    }

    public String toStringShort() {
        return (row + 1) + ":" + (column + 1);
    }

    public String toStringLong() {
        return (row + 1) + ":" + (column + 1) + " in " + sourceFile.getName();
    }
}
