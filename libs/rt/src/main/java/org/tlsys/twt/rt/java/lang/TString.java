package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.ApplyInvoke;
import org.tlsys.twt.CastUtil;
import org.tlsys.twt.JArray;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.*;

@JSClass
@ClassName("java.lang.String")
@ReplaceClass(String.class)
@CodeGenerator(StringCodeGenerator.class)
public final class TString implements TCharSequence {

    public TString(String text) {
    }

    public static String valueOf(long obj) {
        return Script.code(CastUtil.toObject(obj), ".toString()");
    }

    public static String valueOf(int obj) {
        return Script.code(CastUtil.toObject(obj), ".toString()");
    }

    public static String valueOf(short obj) {
        return Script.code(CastUtil.toObject(obj), ".toString()");
    }

    public static String valueOf(byte obj) {
        return Script.code(CastUtil.toObject(obj), ".toString()");
    }

    public static String valueOf(boolean obj) {
        return obj ? "true" : "false";
    }

    public static String valueOf(char obj) {
        return Script.code(CastUtil.toObject(obj));
    }

    public static String valueOf(Object obj) {
        if (obj == null)
            return "null";
        if (obj.getClass() == String.class)
            return CastUtil.cast(obj);

        if (obj.getClass() == Integer.class || obj.getClass() == Float.class)
            return obj.toString();

        //Console.info("Try convert to string...");
        //Console.dir(obj);
        return obj.toString();
    }

    public static String frombyte(byte value) {
        return Byte.toString(value);
    }

    public static String fromshort(short value) {
        return Short.toString(value);
    }

    public static String fromint(int value) {
        return Integer.toString(value);
    }

    public static String fromfloat(float value) {
        return Float.toString(value);
    }

    public static String fromlong(long value) {
        return Long.toString(value);
    }

    public static String fromdouble(double value) {
        return Double.toString(value);
    }

    @MethodAlias("toString")
    @Override
    public String toString() {
        return null;
    }

    @NotCompile
    @InvokeGen(org.tlsys.twt.rt.java.lang.StringInvoke.class)
    @Override
    public native char charAt(int index);

    @NotCompile
    @InvokeGen(org.tlsys.twt.rt.java.lang.StringInvoke.class)
    public native boolean equals(Object obj);

    @NotCompile
    @InvokeGen(org.tlsys.twt.rt.java.lang.StringInvoke.class)
    public native String trim();

    @NotCompile
    @InvokeGen(org.tlsys.twt.rt.java.lang.StringInvoke.class)
    public native String substring(int beginIndex);

    @NotCompile
    @InvokeGen(org.tlsys.twt.rt.java.lang.StringInvoke.class)
    public native String substring(int beginIndex, int endIndex);

    @NotCompile
    @InvokeGen(org.tlsys.twt.rt.java.lang.StringInvoke.class)
    public native boolean isEmpty();

    @NotCompile
    @InvokeGen(org.tlsys.twt.rt.java.lang.StringInvoke.class)
    public native int indexOf(String str);

    @NotCompile
    @InvokeGen(org.tlsys.twt.rt.java.lang.StringInvoke.class)
    public native int indexOf(String str, int fromIndex);

    @NotCompile
    @InvokeGen(org.tlsys.twt.rt.java.lang.StringInvoke.class)
    public native int lastIndexOf(String str);

    @NotCompile
    @InvokeGen(org.tlsys.twt.rt.java.lang.StringInvoke.class)
    public native int lastIndexOf(String str, int fromIndex);

    @NotCompile
    @InvokeGen(org.tlsys.twt.rt.java.lang.StringInvoke.class)
    @Override
    public native int length();

    @NotCompile
    @InvokeGen(org.tlsys.twt.rt.java.lang.StringInvoke.class)
    public native String endsWith(String suffix);

    @InvokeGen(ApplyInvoke.class)
    public String[] split(String regex) {
        return JArray.fromJSArray(Script.code(this,".split(new RegExp(",regex,"))"), String.class);
    }

    @InvokeGen(ApplyInvoke.class)
    public boolean startsWith(String prefix) {
        return indexOf(prefix) == 0;
    }
}
