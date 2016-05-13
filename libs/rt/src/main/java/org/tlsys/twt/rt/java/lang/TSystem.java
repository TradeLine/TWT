package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.rt.java.io.TPrintStream;

@JSClass
@ClassName("java.lang.System")
public class TSystem {
    public static TPrintStream out = new TPrintStream("");
    public static long currentTimeMillis() {
        return CastUtil.toLong(Script.code("Date.now()"));
    }
}
