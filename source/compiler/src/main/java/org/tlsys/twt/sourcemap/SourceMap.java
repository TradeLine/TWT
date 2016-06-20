package org.tlsys.twt.sourcemap;

import org.tlsys.sourcemap.Base64VLQ;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class SourceMap {
    public static interface SourceFile {
        public String getName();

        public String getSource();

        public void writeLines(LineWriter writer);
    }

    private static class State {
        int file = 0;
        int sourceLine = 0;
        int sourceColumn = 0;
        int column = 0;
        int name = 0;
    }

    public final static class LineWriter {
        private final Appendable out;
        private final ArrayList<String> names = new ArrayList<>();

        int state_file = 0;
        int state_sourceLine = 0;
        int state_sourceColumn = 0;
        int state_column = 0;
        int state_name = 0;
        boolean first = true;

        private int file_index = 0;


        public LineWriter(Appendable out) {
            this.out = out;
        }

        public void writeLine(int r, int c, int column) throws IOException {
            if (!first)
                out.append(",");
            first = false;
            Base64VLQ.encode(out, column - state_column);
            state_column = column;

            Base64VLQ.encode(out, file_index - state_file);
            state_file = file_index;

            Base64VLQ.encode(out, r - state_sourceLine);
            state_sourceLine = r;

            Base64VLQ.encode(out, c - state_sourceColumn);
            state_sourceColumn = c;
        }

        public void writeLine(int r, int c, int column, String origenal) throws IOException {
            writeLine(r, c, column);
            int nameIndex = names.indexOf(origenal);
            if (nameIndex == -1) {
                names.add(origenal);
                nameIndex = names.size() - 1;
            }

            Base64VLQ.encode(out, nameIndex - state_name);
            state_name = nameIndex;
        }
    }

    public static interface SourceWriter {
        public OutputStream write(SourceFile file);
    }

    /**
     * Создает файл source-map
     *
     * @param outFileName имя сгенерированного js файла
     * @param outFile поток вывода сгенерированного файла
     * @param writer устройство вывода исходников. Если null, то исходные коды не будут выведены
     * @param files выводимые файлы
     */
    public static void write(String outFileName, OutputStream outFile, SourceWriter writer, SourceFile... files) {

        PrintStream ps = new PrintStream(outFile);

        ps.append("{\n\"version\":3,\n\"file\":\"" + outFileName + "\",\n\"lineCount\":1,\n");
        ps.append("\"mappings\":\"");

        LineWriter lw = new LineWriter(ps);

        int i = 0;
        for (SourceFile sf : files) {
            lw.file_index++;
            sf.writeLines(lw);
        }

        //Вставляем разметку
        ps.append("\",\n");
        ps.append("\"sources\":[");
        boolean first = true;


        for (SourceFile sf : files) {
            if (!first)
                ps.append(",");
            ps.append("\"").append(sf.getName()).append("\"");
            first = false;
        }
        //Вставляем имена файлов
        ps.append("],\n");
        ps.append("\"names\":[");
        first = true;
        for (String s : lw.names) {
            if (!first)
                ps.append(",");
            ps.append("\"").append(s).append("\"");
            first = false;
        }
        //Вставляем имена
        ps.append("]\n}");

        if (writer != null) {
            for (SourceFile sf : files) {
                writer.write(sf);
            }
        }
    }
}
