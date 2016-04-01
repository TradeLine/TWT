package org.tlsys.twt.sourcemap;

import org.junit.Test;

import java.io.IOException;

public class Test1 {

    @Test
    public void test() throws IOException {
        /*
        StringBuilder out = new StringBuilder();
        Base64VLQ.encode(out, 0 - 0);//column
        Base64VLQ.encode(out, 0 - 0);//sourceid
        Base64VLQ.encode(out, 0 - 0);//source line
        Base64VLQ.encode(out, 1 - 0);//source column
        Base64VLQ.encode(out, 0 - 0);//origenal nameid, optional

        System.out.println(out.toString());
        */

        /*
        SourceFile sf = new SourceFile("   ", "simple.js");
        ArrayList<SourceMap.Record> rec = new ArrayList<>();
        rec.add(new SourceMap.Record(sf, sf.getPoint(0, 0), 0, null));
        rec.add(new SourceMap.Record(sf, sf.getPoint(0, 9), 9, "test"));
        rec.add(new SourceMap.Record(sf, sf.getPoint(0, 15), 14, "arg1"));
        rec.add(new SourceMap.Record(sf, sf.getPoint(0, 17), 16, "arg2"));
        rec.add(new SourceMap.Record(sf, sf.getPoint(4, 0), 41, "test"));

        System.out.println("=>>\n" + new SourceMap(rec).generate());
        */
    }
}
