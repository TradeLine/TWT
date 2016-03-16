package org.tlsys.twt.rt.java.util;

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
        data = Script.code("new Date(",time,")");
    }

    public TData() {
        data = Script.code("new Date()");
    }

    public long getTime() {
        return Script.code(data,".getTime()");
    }

    public void setTime(long time) {
        Script.code(data,".setTime(",time,")");
    }

    public int getMonth() {
        int d = Script.code(data,".getMonth()");
        return d + 1;
    }

    public int getDate() {
        return Script.code(data,".getDate()");
    }

    @Deprecated
    public int getYear() {
        return Script.code(data,".getFullYear()");
    }
}
