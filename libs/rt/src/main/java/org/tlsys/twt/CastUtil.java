package org.tlsys.twt;

import org.tlsys.twt.annotations.InvokeGen;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.NotCompile;

@JSClass
@NotCompile
public final class CastUtil {
    private CastUtil() {
    }

    @InvokeGen(org.tlsys.twt.CastInvoke.class)
    public native static <T> T cast(Object obj);

    @InvokeGen(org.tlsys.twt.CastInvoke.class)
    public native static int toInt(Object obj);

    @InvokeGen(org.tlsys.twt.CastInvoke.class)
    public native static float toFloat(Object object);

    @InvokeGen(org.tlsys.twt.CastInvoke.class)
    public native static double toDouble(Object obj);

    @InvokeGen(org.tlsys.twt.CastInvoke.class)
    public native static long toLong(Object obj);

    @InvokeGen(org.tlsys.twt.CastInvoke.class)
    public native static byte toByte(Object obj);

    @InvokeGen(org.tlsys.twt.CastInvoke.class)
    public native static char toChar(Object obj);

    @InvokeGen(org.tlsys.twt.CastInvoke.class)
    public native static short toShort(Object obj);

    @InvokeGen(org.tlsys.twt.CastInvoke.class)
    public native static boolean toBoolean(Object obj);

    @InvokeGen(org.tlsys.twt.CastInvoke.class)
    public native static Object toObject(boolean obj);

    @InvokeGen(org.tlsys.twt.CastInvoke.class)
    public native static Object toObject(char obj);

    @InvokeGen(org.tlsys.twt.CastInvoke.class)
    public native static Object toObject(int obj);

    @InvokeGen(org.tlsys.twt.CastInvoke.class)
    public native static Object toObject(double obj);

    @InvokeGen(org.tlsys.twt.CastInvoke.class)
    public native static Object toObject(long obj);

    @InvokeGen(org.tlsys.twt.CastInvoke.class)
    public native static Object toObject(byte obj);

    @InvokeGen(org.tlsys.twt.CastInvoke.class)
    public native static Object toObject(short obj);

    @InvokeGen(org.tlsys.twt.CastInvoke.class)
    public native static byte intToByte(int obj);
}
