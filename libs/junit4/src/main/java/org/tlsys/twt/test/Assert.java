package org.tlsys.twt.test;

import org.tlsys.twt.annotations.JSClass;

@JSClass
public class Assert {
    public static void assertTrue(boolean value) {
        if (!value)
            throw new TWTBooleanException(true, value);
    }

    public static void assertFalse(boolean value) {
        if (value)
            throw new TWTBooleanException(false, value);
    }
}
