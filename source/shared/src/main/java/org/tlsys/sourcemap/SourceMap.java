package org.tlsys.sourcemap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class SourceMap {

    public static String generate(Collection<Record> records) throws IOException {

        State state = new State();

        for (Record r : records) {
            if (!state.files.contains(r.getFile()))
                state.files.add(r.getFile());

            if (r.getName() != null) {
                if (!state.names.contains(r.getName()))
                    state.names.add(r.getName());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\n\"version\":3,\n\"file\":\"out.js\",\n\"lineCount\":1,\n");
        sb.append("\"mappings\":\"");
        boolean first = true;
        for (Record r : records) {
            if (!first)
                sb.append(",");
            r.write(sb, state);
            first = false;
        }
        //Вставляем разметку
        sb.append("\",\n");
        sb.append("\"sources\":[");
        first = true;
        for (SourceFile sf : state.files) {
            if (!first)
                sb.append(",");
            sb.append("\"").append(sf.getName()).append("\"");
            first = false;
        }
        //Вставляем имена файлов
        sb.append("],\n");
        sb.append("\"names\":[");
        first = true;
        for (String s : state.names) {
            if (!first)
                sb.append(",");
            sb.append("\"").append(s).append("\"");
            first = false;
        }
        //Вставляем имена
        sb.append("]\n}");

        return sb.toString();
    }

    private static class State {
        int file = 0;
        int sourceLine = 0;
        int sourceColumn = 0;
        int column = 0;
        int name = 0;
        ArrayList<SourceFile> files = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
    }

    public static class Record {
        private final SourceFile file;
        private final SourcePoint point;
        private final int column;
        private final String name;

        public Record(SourceFile file, SourcePoint point, int column, String name) {
            this.file = file;
            this.point = point;
            this.column = column;
            this.name = name;
        }

        public SourceFile getFile() {
            return file;
        }

        public SourcePoint getPoint() {
            return point;
        }

        public int getColumn() {
            return column;
        }

        public String getName() {
            return name;
        }

        public void write(Appendable out, State state) throws IOException {
            Base64VLQ.encode(out, column - state.column);
            state.column = column;

            int fileIndex = state.files.indexOf(file);
            Base64VLQ.encode(out, fileIndex - state.file);
            state.file = fileIndex;

            Base64VLQ.encode(out, point.getRow() - state.sourceLine);
            state.sourceLine = point.getRow();

            Base64VLQ.encode(out, point.getColumn() - state.sourceColumn);
            state.sourceColumn = point.getColumn();

            if (name != null) {
                int nameIndex = state.names.indexOf(name);
                Base64VLQ.encode(out, nameIndex - state.name);
                state.name = nameIndex;
            }
        }
    }
}
