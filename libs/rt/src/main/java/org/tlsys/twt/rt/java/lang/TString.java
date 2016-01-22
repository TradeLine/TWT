package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.*;

@JSClass
@ClassName("java.lang.String")
@ReplaceClass(String.class)
@CodeGenerator(NativeCodeGenerator.class)
public final class TString {

    public TString(String text) {
    }

    @MethodAlias("toString")
    @Override
    public String toString() {
        return super.toString();
    }


    @NotCompile
    @InvokeGen(org.tlsys.twt.rt.java.lang.StringInvoke.class)
    public native char charAt(int index);

    @NotCompile
    @InvokeGen(org.tlsys.twt.rt.java.lang.StringInvoke.class)
    public native boolean equals(Object obj);

    //@NotCompile
    //@InvokeGen("org.tlsys.twt.rt.java.lang.StringInvoke")
    public static String valueOf(Object obj) {
        if (obj == null)
            return "null";
        return obj.toString();
    }

    //@NotCompile
    //@InvokeGen("org.tlsys.twt.rt.java.lang.StringInvoke")
    public static String valueOf(int obj) {
        return Script.code(obj,".toString()");
    }

    //@NotCompile
    //@InvokeGen("org.tlsys.twt.rt.java.lang.StringInvoke")
    public static String valueOf(long obj) {
        return Script.code(obj,".toString()");
    }

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
    public native int length();
}
