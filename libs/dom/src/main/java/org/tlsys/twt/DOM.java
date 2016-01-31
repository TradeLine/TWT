package org.tlsys.twt;

import org.tlsys.twt.annotations.JSClass;

@JSClass
public class DOM {
    public static void appendChild(Object to, Object what) {
        Script.code(to, ".appendChild(",what,")");
    }
}
