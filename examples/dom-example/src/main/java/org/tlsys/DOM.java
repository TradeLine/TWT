package org.tlsys;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class DOM {
    public static void setHTML(Object dom, String html) {
        Script.code(dom,".innerHTML=",html);
    }

    public static String getHTML(Object dom) {
        return Script.code(dom,".innerHTML");
    }
}
