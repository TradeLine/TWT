package org.tlsys.sourcemap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SourceMap {

    private HashMap<SourceFile, FileRecord> files = new HashMap<>();
    private ArrayList<String> names = new ArrayList<>();

    //private Collection<Record> records;

    private static class FileRecord {
        private final int id;
        private final SourceFile file;
        private final ArrayList<Record> records = new ArrayList<>();

        public FileRecord(int id, SourceFile file) {
            this.file = file;
            this.id = id;
        }

        public ArrayList<Record> getRecords() {
            return records;
        }

        public SourceFile getFile() {
            return file;
        }

        public void sort() {
            records.sort((a,b)->{
                int len = a.point.getRow() - b.point.getRow();
                if (len != 0)
                    return len;

                int col = a.point.getColumn() - b.point.getColumn();
                return col;
            });
        }
    }

    private FileRecord getOrCreateFileRecord(SourceFile sf) {
        FileRecord fr = files.get(sf);
        if (fr != null)
            return fr;
        fr = new FileRecord(files.keySet().size(), sf);
        files.put(sf, fr);
        return fr;
    }

    public SourceMap(Collection<Record> records) {
        //this.records = records;
        for (Record r : records) {
            getOrCreateFileRecord(r.getFile()).getRecords().add(r);
            /*
            if (!files.contains(r.getFile()))
                files.add(r.getFile());
                */

            if (r.getName() != null) {
                if (!names.contains(r.getName()))
                    names.add(r.getName());
            }
        }

        for (FileRecord fr : files.values()) {
            fr.sort();
        }
    }

    public String generate() throws IOException {
        StringBuilder sb = new StringBuilder();
        generate(sb);
        return sb.toString();
    }

    public void generate(Appendable sb) throws IOException {
        sb.append("{\n\"version\":3,\n\"file\":\"out.js\",\n\"lineCount\":1,\n");
        sb.append("\"mappings\":\"");
        boolean first = true;
        State state = new State();
        state.map = this;
        for (FileRecord fr : files.values()) {
            /*
            state.column = 0;
            state.name = 0;
            state.name = 0;
            state.file = 0;
            */
            for (Record r : fr.records) {
                if (!first)
                    sb.append(",");
                r.write(sb, state);
                first = false;
            }
        }
        //Вставляем разметку
        sb.append("\",\n");
        sb.append("\"sources\":[");
        first = true;

        ArrayList<FileRecord> fls = new ArrayList<FileRecord>(files.values());
        fls.sort((a,b)->a.id-b.id);
        for (FileRecord sf : fls) {
            if (!first)
                sb.append(",");
            sb.append("\"").append(sf.file.getName()).append("\"");
            first = false;
        }
        //Вставляем имена файлов
        sb.append("],\n");
        sb.append("\"names\":[");
        first = true;
        for (String s : names) {
            if (!first)
                sb.append(",");
            sb.append("\"").append(s).append("\"");
            first = false;
        }
        //Вставляем имена
        sb.append("]\n}");
    }

    public Collection<SourceFile> getFiles() {
        return files.keySet();
    }

    private static class State {
        int file = 0;
        int sourceLine = 0;
        int sourceColumn = 0;
        int column = 0;
        int name = 0;
        SourceMap map;
    }

    private int getFileId(SourceFile sf) {
        return getOrCreateFileRecord(sf).id;
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

        /*
        public int getColumn() {
            return column;
        }
        */

        public String getName() {
            return name;
        }

        public void write(Appendable out, State state) throws IOException {

            System.out.println("--->" + getFile().getName() + " " + point.getRow()+":" + point.getColumn() + "==>" + column);

            Base64VLQ.encode(out, column - state.column);
            state.column = column;

            int fileIndex = state.map.getFileId(file);
            Base64VLQ.encode(out, fileIndex - state.file);
            state.file = fileIndex;

            Base64VLQ.encode(out, point.getRow() - state.sourceLine);
            state.sourceLine = point.getRow();

            Base64VLQ.encode(out, point.getColumn() - state.sourceColumn);
            state.sourceColumn = point.getColumn();

            if (name != null) {
                int nameIndex = state.map.names.indexOf(name);
                Base64VLQ.encode(out, nameIndex - state.name);
                state.name = nameIndex;
            }
        }
    }
}
