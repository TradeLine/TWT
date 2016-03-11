package org.tlsys.twt.rt.java.util.regex;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

@JSClass
@ReplaceClass(java.util.regex.Pattern.class)
public class TPattern implements Serializable {

    public static final int CASE_INSENSITIVE = 0x02;
    //public static final int COMMENTS = 0x04;
    public static final int MULTILINE = 0x08;
    //public static final int LITERAL = 0x10;
    //public static final int DOTALL = 0x20;

    public static Pattern compile(String regex) {
        return CastUtil.cast(new TPattern(regex, 0));
    }

    public static Pattern compile(String regex, int flags) {
        return CastUtil.cast(new TPattern(regex, flags));
    }

    private final Objects js;

    private TPattern(String pattern, int flags) {
        String ff ="";
        if ((flags & CASE_INSENSITIVE) == 1) {
            ff = ff + "i";
        }
        if ((flags & MULTILINE) == 1) {
            ff = ff + "m";
        }

        js = Script.code("new RegExp(",pattern,", ",ff,")");
    }
}
