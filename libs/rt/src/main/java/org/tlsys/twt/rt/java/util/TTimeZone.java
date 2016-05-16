package org.tlsys.twt.rt.java.util;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;
import org.tlsys.twt.rt.twt.util.TZoneInfo;

import java.util.Optional;
import java.util.TimeZone;

@JSClass
@ReplaceClass(TimeZone.class)
public abstract class TTimeZone implements Cloneable {


    private static TTimeZone defaultTimeZone;

    static {
        resetDefault();
    }

    private String ID;

    private static void resetDefault() {
        Optional<TTimeZone> opt = TZoneInfo.getZoneByOffset(CastUtil.toInt(Script.code("-(new Date().getTimezoneOffset()*1000*60)")));

        if (opt.isPresent())
            defaultTimeZone = opt.get();
        else
            defaultTimeZone = TZoneInfo.getZoneByOffset(0).get();
    }

    public static TTimeZone getDefault() {
        return (TTimeZone) defaultTimeZone.clone();
    }

    public static void setDefault(TTimeZone zone) {
        defaultTimeZone = zone;
    }

    public abstract int getRawOffset();

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Object clone() {
        try {
            TTimeZone other = (TTimeZone) super.clone();
            other.ID = ID;
            return other;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}
