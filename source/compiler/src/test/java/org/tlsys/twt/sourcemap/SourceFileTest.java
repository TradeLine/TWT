package org.tlsys.twt.sourcemap;

import org.junit.Test;
import org.tlsys.sourcemap.SourceFile;

import static org.junit.Assert.assertEquals;


public class SourceFileTest {

    @Test
    public void testFirstLine() {
        SourceFile sf = new SourceFile("012\n345\n678", "");
        assertEquals(sf.getLineOfIndex(2), 0);


        sf = new SourceFile("012345\n678", "");
        assertEquals(sf.getLineOfIndex(6), 1);


        sf = new SourceFile("012345\n789abcdefg", "");
        assertEquals(sf.getLineOfIndex(9), 1);
        assertEquals(sf.getLineOfIndex(7), 1);
        assertEquals(sf.getLineOfIndex(6), 1);
    }
}
