package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;
import org.tlsys.twt.rt.boxcastadapter.BoxCastAdapter;

@JSClass
@ReplaceClass(java.lang.Boolean.class)
@CastAdapter(BoxCastAdapter.class)
public class TBoolean {
    private final boolean value;

    public TBoolean(boolean value) {
        this.value = value;
    }

    public static boolean parseBoolean(String s) {
        return ((s != null) && s.equals("true"));
    }

    public static String toString(boolean b) {
        return b ? "true" : "false";
    }

    public static int hashCode(boolean value) {
        return value ? 1231 : 1237;
    }

    public boolean booleanValue() {
        return value;
    }

    public String toString() {
        return booleanValue() ? "true" : "false";
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

    public boolean equals(Object obj) {
        if (obj instanceof Boolean) {
            return value == ((Boolean) obj).booleanValue();
        }
        return false;
    }

    public static TBoolean fromjava_lang_Object(Object value) {
        return CastUtil.cast(value);
    }
}
