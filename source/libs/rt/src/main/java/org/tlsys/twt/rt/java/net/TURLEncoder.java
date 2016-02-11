package org.tlsys.twt.rt.java.net;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@ClassName("java.net.URLEncoder")
public final class TURLEncoder {
    private TURLEncoder() {
    }

    public static String encode(String s) {
        return Script.code("encodeURIComponent(",s,")");
    }

    public static String encode(String s, String enc) {
        return encode(s);
    }
}
