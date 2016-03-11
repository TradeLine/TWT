package org.tlsys.twt.dom;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class DomStyle {
    private final Object style;

    public DomStyle(Object style) {
        this.style = style;
    }

    public String get(String name) {
        String s = Script.code(style, "[", name, "]");
        s = s + "";
        return s;
    }

    public DomStyle set(String name, String value) {
        Script.code(style, "[", name, "]=", value);
        return this;
    }

    public DomStyle setPx(String name, int value) {
        return set(name, value+"px");
    }

    public DomStyle setP(String name, int value) {
        return set(name, value+"%");
    }
}
