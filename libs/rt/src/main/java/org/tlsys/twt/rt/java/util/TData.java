package org.tlsys.twt.rt.java.util;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

import java.util.Date;

@JSClass
@ClassName("java.util.Date")
@ReplaceClass(Date.class)
public class TData {
    private transient Object data;

    public TData(long time) {
        data = Script.code("new Date(",CastUtil.toObject(time),")");
    }

    public TData() {
        data = Script.code("new Date()");
    }

    public long getTime() {
        return CastUtil.toLong(Script.code(data,".getTime()"));
    }

    public void setTime(long time) {
        Script.code(data,".setTime(",CastUtil.toObject(time),")");
    }

    public int getMonth() {
        int d = CastUtil.toInt(Script.code(data, ".getMonth()"));
        return d + 1;
    }

    public int getDate() {
        return CastUtil.toInt(Script.code(data,".getDate()"));
    }

    @Deprecated
    public int getYear() {
        return CastUtil.toInt(Script.code(data,".getFullYear()"));
    }

    @Deprecated
    public int getDay() {
        return CastUtil.toInt(Script.code(data, ".getDay()"));
    }
}
