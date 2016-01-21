package org.tlsys.twt;

import org.tlsys.twt.annotations.InvokeGen;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.NotCompile;

@JSClass
@NotCompile
public final class CastUtil {
    private CastUtil() {
    }

    @InvokeGen("org.tlsys.twt.CastInvoke")
    public native static <T> T cast(Object obj);
}
