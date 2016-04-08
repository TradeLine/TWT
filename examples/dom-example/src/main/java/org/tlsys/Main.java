package org.tlsys;

import org.tlsys.twt.Console;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.DomNode;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.dom.DOM;
import org.tlsys.twt.dom.Document;
import org.tlsys.twt.events.Events;

import java.util.function.Function;

@JSClass
public class Main {

    /*
    private static InitClass stat = new InitClass("STATIC FIELD");
    private int aaa;
    private InitClass non_stat = new InitClass("NONSTATIC FIELD");
    */


    static {
        Console.info("HELLO FROM STATIC BLOCK");
    }

    public Main(int aaab) {
        Console.info("???");
        //aaa = aaab;

        Function<Void, Void> f = (a) -> {
            Console.info("Hello from LAMBDA IN CLASS " + this);
            return null;
        };

        f.apply(null);
    }

    public static void giveException() {
        throw new RuntimeException("ERROR!");
    }

    public static void main() {


        Object o2 = JsonReader.jsonToObject("{\"@type\":\"org.tlsys.Response\",\"id\":11,\"body\":\"1111 from serevr\"}");
        Console.info("OUT=");
        Console.dir(o2);



        /*
        Tabs tabs = new Tabs();
        tabs.createTab("Карты");
        tabs.createTab("Контакты");
        tabs.createTab("Рефералы");

        tabs.setActiveTab(1);

        tabs.addListener((o,n)->{
            Console.info("Changed to " + n.getTitle());
        });



        DOM.appendChild(o, tabs);

        */

        /*
        doit1(8);
        doit2(new Integer(10));
        Console.info(""+(byte)129);

        for (int a = -300; a <= 300; a++) {
            Console.info("->" + a + " ===> " + ((byte)a));
        }
*/
        /*
        Console.info("->");


        new Main(1).doit();
        new Main(2).doit();
        new Main(3).doit();
        */


        //Console.info("Timer setted! " + id);



        API api = new API((s)->{
            Console.info("CONNECTED!");
        });

        MyBtn b1 = new MyBtn("Send 1");
        MyBtn b2 = new MyBtn("Send 2");

        Object o = DOM.getElementsByTagName(Document.get(), "body")[0];

        DOM.appendChild(o, b1);
        DOM.appendChild(o, b2);

        Events.addEventListener(b1, "click", (s, e)->{
            SecondApi sa = new SecondApi(api, 1);
            sa.send("1111");
            Console.info("Send1");
        }, false);

        Events.addEventListener(b2, "click", (s,e)->{
            SecondApi sa = new SecondApi(api, 2);
            sa.send("2222");
            Console.info("Send2");

            Script.setTimeout(1000, ()->{
                Console.dir(b2);
            });
        }, false);


    }

    @DomNode("button")
    private static class MyBtn {
        public MyBtn(String text) {
            DOM.setHTML(this, text);
        }
    }

/*
    public void doit() {

        int a = 9;

        Script.TimeoutCallback t = new Script.TimeoutCallback(){
            @Override
            @ForceInject
            public void onTimeout() {

                Console.info("IS CALL BACK! " + aaa + "   " + a);
                Console.info(this.getClass().getName());

                new SubClass().tt();
            }
        };


        Script.setTimeout(5000, new Script.TimeoutCallback() {
            @ForceInject
            @Override
            public void onTimeout() {
                Main.this.giveException();
            }
        });


        long id = Script.setTimeout(1000, t);
    }

    private static class SubInitClass {
        public SubInitClass(String text) {
            Console.info("SUB-INIT! " + text);
        }
    }

    private static class InitClass extends SubInitClass {
        public InitClass(String text) {
            super("!!!");
            Console.info("INIT! " + text);
        }
    }

    public class SubClass {
        void tt() {
            Console.info("Hello from SUBCLASS!");
            Console.info("aaa=" + Main.this.aaa);
        }
    }
    */
}
