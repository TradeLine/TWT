package org.tlsys.twt;

import org.junit.Test;
import static org.junit.Assert.*;

public class StyleTest {
    @Test
    public void testFromPx() {
        assertEquals(Style.fromPx("101px"), 101);
    }

    @Test
    public void testToPx() {
        assertEquals(Style.toPx(101), "101px");
    }
}
