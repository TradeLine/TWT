package org.tlsys;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.classes.ArrayBuilder;
import org.tlsys.twt.rt.java.lang.TClass;

@JSClass
public class Main extends Parent<String> {

    private static final Events.EventListener ee = (s, e) -> {
        info("CLICK1");
        //removeEvent(s);
    };

    private static void removeEvent(Object dom) {
        Events.removeEventListener(dom, "click", ee, false);
    }

    private static void attach(Object dom) {
        Script.code("document.getElementsByTagName('body')[0].appendChild(", dom, ")");
    }

    private static void info(String text) {
        Script.code("console.info(", text, ")");
    }

    private static void dir(Object text) {
        Script.code("console.dir(", text, ")");
    }

    public Main() {
        Butten t1 = new Butten();
        attach(t1);
        DOM.setHTML(t1, "test1");

        Butten t2 = new Butten();
        attach(t2);
        DOM.setHTML(t2, "test2");

        Butten t3 = new Butten();
        attach(t3);
        DOM.setHTML(t3, "test3");

        info("Event listener=");
        dir(ee);
        info("------------");

        Events.EventListener el3 = (s, e) -> {
            info("CLICK3");
        };

        Events.addEventListener(t1, "click", ee, false);
        Events.addEventListener(t2, "click", (s, e) -> {
            info("CLICK2");
            dir(t2);
            Events.removeEventListener(t3, "click", el3, false);
        }, false);

        Events.addEventListener(t3, "click", el3, false);
    }

    @Override
    public void doit(String val) {
        Script.code("console.info('Parent::doit')");
        super.doit(val);
    }

    public static void print(String... list) {
        for (String s : list) {
            Script.code("console.info('-->'+", s, ")");
        }
    }

    public static void main() {
        Main m = new Main();
        Script.code("console.info('Hello from Console')");
    }
}
