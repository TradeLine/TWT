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

    public TData(int year, int month, int day) {
        data = Script.code("new Date(", CastUtil.toObject(year), ",", CastUtil.toObject(month), ",", CastUtil.toObject(day), ")");
    }

    public long getTime() {
        return CastUtil.toLong(Script.code(data,".getTime()"));
    }

    public void setTime(long time) {
        Script.code(data,".setTime(",CastUtil.toObject(time),")");
    }

    public int getMonth() {
        return CastUtil.toInt(Script.code(data, ".getMonth()"));
    }


    public void setMonth(long month) {
        Script.code(data, ".setMonth(", CastUtil.toObject(month), ")");
    }

    public int getDate() {
        return CastUtil.toInt(Script.code(data,".getDate()"));
    }

    public void setDate(int date) {
        Script.code(data, ".setDate(", CastUtil.toObject(date), ")");
    }

    @Deprecated
    public int getYear() {
        return CastUtil.toInt(Script.code(data,".getFullYear()"));
    }

    public void setYear(long year) {
        Script.code(data, ".setFullYear(", CastUtil.toObject(year), ")");
    }


    @Deprecated
    public int getDay() {
        return CastUtil.toInt(Script.code(data, ".getDay()"));
    }

    public int getMilliseconds() {
        return CastUtil.toInt(Script.code(data, ".getMilliseconds()"));
    }

    public void setMilliseconds(int milliseconds) {
        Script.code(data, ".setMilliseconds(", CastUtil.toObject(milliseconds), ")");
    }

    public int getSeconds() {
        return CastUtil.toInt(Script.code(data, ".getSeconds()"));
    }

    public void setSeconds(int seconds) {
        Script.code(data, ".setSeconds(", CastUtil.toObject(seconds), ")");
    }

    public int getHours() {
        return CastUtil.toInt(Script.code(data, ".getHours()"));
    }

    public void setHours(int hours) {
        Script.code(data, ".setHours(", CastUtil.toObject(hours), ")");
    }


}
