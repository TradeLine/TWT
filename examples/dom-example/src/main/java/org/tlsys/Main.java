package org.tlsys;

import org.tlsys.twt.Console;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.dom.DOM;
import org.tlsys.twt.dom.Document;
import org.tlsys.twt.events.Events;
import org.tlsys.twt.json.Json;

@JSClass
public class Main {

    public static void doit1(Integer a) {
        Console.info("INTEGER->"+a);
    }

    public static long doit2(int a) {
        Console.info("INT=>"+a);
        return a;
    }


    public static void main() {
        doit1(8);
        doit2(new Integer(10));
        Console.info(""+(byte)129);

        for (int a = -300; a <= 300; a++) {
            Console.info("->" + a + " ===> " + ((byte)a));
        }

        long id = Script.setTimeout(5000, ()->{
            Console.info("IS CALL BACK!");
        });

        Console.info("Timer setted! " + id);

        API api = new API((s)->{
            Console.info("CONNECTED!");
        });

        Button b1 = new Button("Send 1");
        Button b2 = new Button("Send 2");

        Object o = DOM.getElementsByTagName(Document.get(), "body")[0];

        DOM.appendChild(o, b1);
        DOM.appendChild(o, b2);

        Events.addEventListener(b1, "click", (s,e)->{
            Console.info("Send1");
            api.call((s2->{
                Console.info("resived " + s2);
            }), "REQUEST-1");
        }, false);

        Events.addEventListener(b2, "click", (s,e)->{
            Console.info("Send2");
            api.call((s2->{
                Console.info("resived " + s2);
            }), "REQUEST-2");
        }, false);
    }
}
