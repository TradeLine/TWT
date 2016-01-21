package org.tlsys.twt.rt;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@ClassName(value = "char", primitive = true, nativeName = "C")
public class Tchar {
    private String body;

    public Tchar(String _body) {
        body = _body;
        this.body = _body;
    }

    @Override
    public String toString() {
        return body;
    }
}
