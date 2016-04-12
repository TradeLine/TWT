package org.tlsys.twt;

import org.junit.Test;
import org.tlsys.sourcemap.Base64VLQ;
import org.tlsys.sourcemap.SourceFile;
import org.tlsys.sourcemap.SourceMap;

import java.io.IOException;
import java.util.ArrayList;

public class SourceMapTest {


    @Test
    public void test() throws IOException {
        SourceFile sf1 = new SourceFile("aaa bbb ccc", "test1", new OneLinePosProvider());

        ArrayList<SourceMap.Record> recs = new ArrayList<>();
        recs.add(new SourceMap.Record(sf1, sf1.getPoint(0), 0, null));
        recs.add(new SourceMap.Record(sf1, sf1.getPoint(4), 1, null));
        recs.add(new SourceMap.Record(sf1, sf1.getPoint(8), 2, null));

        SourceFile sf2 = new SourceFile("aaa bbb ccc", "test2", new OneLinePosProvider());

        recs.add(new SourceMap.Record(sf2, sf2.getPoint(0), 0, null));
        recs.add(new SourceMap.Record(sf2, sf2.getPoint(4), 1, null));
        recs.add(new SourceMap.Record(sf2, sf2.getPoint(8), 2, null));

        System.out.printf("=>\n" + new SourceMap(recs).generate()+"\n\n");
    }

    @Test
    public void test1() {
        Base64VLQ.decode()
    }

    private static class OneLinePosProvider implements SourceFile.PositionProvider {

        @Override
        public int getLine(int pos) {
            return 0;
        }

        @Override
        public int getColumn(int pos) {
            return pos;
        }

        @Override
        public int getIndex(int row, int column) {
            return 0;
        }
    }
}
