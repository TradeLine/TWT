package org.tlsys.twt.dom;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class ClassList {
    private final Object classList;

    public ClassList(Object classList) {
        this.classList = classList;
    }

    public int length() {
        return CastUtil.toInt(Script.code(classList, ".length"));
    }

    public String get(int index) {
        return Script.code(classList, ".item(", CastUtil.toObject(index), ")");
    }

    public void remove(String... names) {
        for (String s : names)
            Script.code(classList, ".remove(", s, ")");
    }

    public void add(String... names) {
        for (String s : names)
            Script.code(classList, ".add(", s, ")");
    }

    public void toggle(String name) {
        Script.code(classList, ".toggle(", name, ")");
    }

    public boolean contains(String name) {
        return Script.code(classList, ".contains(", name, ")");
    }

    public boolean containsAll(String... names) {
        for (String s : names)
            if (!contains(s))
                return false;
        return true;
    }
}
