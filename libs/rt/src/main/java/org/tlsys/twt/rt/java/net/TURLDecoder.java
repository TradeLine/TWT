package org.tlsys.twt.rt.java.net;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@ClassName("java.events.URLDecoder")
public class TURLDecoder {

    public static String decode(String s) {
        return Script.code("decodeURIComponent(",s,")");
    }

    public static String decode(String s, String enc) {
        return decode(s);
    }
}
