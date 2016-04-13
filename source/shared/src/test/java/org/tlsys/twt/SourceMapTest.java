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

        System.out.printf("=>\n" + new SourceMap(recs).generate() + "\n\n");
    }

    @Test
    public void test1() {
        String data = "AAAAA,QAASA,KAAI,CAACC,CAAD,CAAOC,CAAP,CAAaC,CAAb,CAAmB,CAC/BF,CAAA,CAAOC,CACPD,EAAA,CAAOC,CAAAE,KAAA,EAAAC,KAAA,CAAiBH,CAAjB,CAAsBD,CAAtB,CACPK,QAAAC,KAAA,CAAaN,CAAb,CAAoB,GAApB,CAA0BC,CAA1B,CAGID,EAAJ,EAAYE,CAAAK,SAAA,EAAZ,GACCP,CADD,CACMQ,EAAA,CAAGC,EAAH,CAAMC,EAAN,CAASC,EADf,CAKA,OAAOX,EAXwB,CAchCD,IAAA,CAAK,GAAL,CAAU,GAAV;";
        String[] names = "test,arg1,arg2,arg3,get1,get0,console,info,getValue,a1,a2,a3,a4".split(",");

        for (String line : data.split(";")) {
            int file = 0;
            int name = 0;
            int column = 0;
            int row = 0;
            int pos = 0;

            for (String item : line.split(",")) {
                //System.out.println("->" + item);
                StringCharIterator sc = new StringCharIterator(item);
                pos += Base64VLQ.decode(sc);
                file += Base64VLQ.decode(sc);
                row += Base64VLQ.decode(sc);
                column += Base64VLQ.decode(sc);
                //System.out.println("\tPOS=" + (pos + 1));
                //System.out.println("\tFILE=" + file);
                System.out.print("\tSOURCE  " + (row + 1) + ":\t" + (column + 1));
                if (sc.hasNext()) {
                    name += Base64VLQ.decode(sc);
                    System.out.print("\t\"" + names[name] + "\"");
                } else
                    System.out.print("\t\t");

                System.out.println("\t==> " + (pos + 1));
                if (sc.hasNext())
                    throw new RuntimeException("Not all was readded");
            }
        }


    }

    private static class StringCharIterator implements Base64VLQ.CharIterator {

        private final String data;
        private int pos = -1;

        private StringCharIterator(String data) {
            this.data = data;
        }

        @Override
        public boolean hasNext() {
            return pos + 1 < data.length();
        }

        @Override
        public char next() {
            return data.charAt(++pos);
        }
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
