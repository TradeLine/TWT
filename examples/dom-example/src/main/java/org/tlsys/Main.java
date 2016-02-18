package org.tlsys;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.classes.ArrayBuilder;
import org.tlsys.twt.dom.DOM;
import org.tlsys.twt.dom.Document;
import org.tlsys.twt.rt.java.lang.TClass;

@JSClass
public class Main extends Parent<String> {

    private static void attach(Object dom) {
        //DOM.appendChild(Document.get(), dom)
        Script.code(Document.get(),".getElementsByTagName('body')[0].appendChild(", dom, ")");
    }

    private static void info(String text) {
        Script.code("console.info(", text, ")");
    }

    private static void dir(Object text) {
        Script.code("console.dir(", text, ")");
    }

    public Main() {
        Butten t1 = new Butten("test 1");
        attach(t1);

        Butten t2 = new Butten();
        t2.setTitle("111111111");
        attach(t2);

        Butten t3 = new Butten("test 3");
        attach(t3);

        info("Event listener=");
        info("------------");

        Events.EventListener el3 = (s, e) -> {
            //Ajax.Result rs = Ajax.create("http://127.0.0.1/test.php").get().sync();
            //dir(rs);
            RuntimeException ee = new RuntimeException("HELLO!");
            dir(ee);
            dir(ee.getClass());
        };

        Events.addEventListener(t2, "click", (s, e) -> {
            info("CLICK2");
            User u = new User("Hello");
            new User();
            u.list = new String[3];
            u.list[0]="el1";
            u.list[1]="el2";
            u.list[2]="el3";
            String json =Json.toJSON(u);
            Object o = Json.fromJSON(json);
            info(json);
            dir(o);
            /*

            dir(t2);
            Events.removeEventListener(t3, "click", el3, false);
            */
            /*
            try {
                info("11");
                dir(t2.getClass().newInstance());
                info("22");
            } catch (InstantiationException e1) {
                info("ERROR1");
            } catch (IllegalAccessException e1) {
                info("ERROR2");
            }
            */
        }, false);

        Events.addEventListener(t3, "click", el3, false);
    }

    @Override
    public void doit(String val) {
        Script.code("console.info('Parent::doit')");
        super.doit(val);
    }

    public static void main() {
        Main m = new Main();
    }
}
