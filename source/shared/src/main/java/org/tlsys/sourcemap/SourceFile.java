package org.tlsys.sourcemap;

import java.io.Serializable;

/**
 * Класс исходного файла
 */
public class SourceFile implements Serializable {
    private static final long serialVersionUID = 106399079283455885L;
    private final String data;
    private final String name;
    //private final int[] brs;
    private transient PositionProvider positionProvider;

    public SourceFile(String data, String name, PositionProvider positionProvider) {
        this.data = data;
        this.name = name;
        this.positionProvider = positionProvider;
        /*
        ArrayList<Integer> lines = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == '\n')
                lines.add(i);
        }


        brs = new int[lines.size()];
        int c = 0;
        for (Integer j : lines) {
            brs[c++] = j;
        }
        */
    }

    public String getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    /*
        int getLineOfIndex(int index) {
            if (index < 0)
                throw new IllegalArgumentException("Bad index: " + index);
            if (brs.length <= 0) {
                if (index >= data.length())
                    throw new IllegalArgumentException("Out of data: data len = " + data.length() + ", index =" + index);
                return 0;
            }
            if (brs[0] > index)
                return 0;
            for (int i = 1; i < brs.length; i++) {
                if (index >= brs[i - 1] && index < brs[i])
                    return i;
            }

            if (index >= data.length())
                throw new IllegalArgumentException("Out of data: data len = " + data.length() + ", index =" + index);

            return brs.length;
        }
    */
    public SourcePoint getPoint(int index) {
        return new SourcePoint(positionProvider.getLine(index), positionProvider.getColumn(index), this);
        /*
        int line = getLineOfIndex(index);
        if (line == 0)
            return new SourcePoint(line, index, this);
        return new SourcePoint(line, index - brs[line - 1], this);
        */
    }

    public SourcePoint getPoint(int row, int column) {
        return new SourcePoint(row, column, this);
    }

    public interface PositionProvider {
        public int getLine(int pos);

        public int getColumn(int pos);
    }
}
