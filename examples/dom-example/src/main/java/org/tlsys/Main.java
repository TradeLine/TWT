package org.tlsys;

import org.tlsys.twt.Console;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.ForceInject;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class Main {

    private int aaa;

    public Main(int aaab) {
        aaa = aaab;
    }

    public static void main() {

        /*
        Tabs tabs = new Tabs();
        tabs.createTab("Карты");
        tabs.createTab("Контакты");
        tabs.createTab("Рефералы");

        tabs.setActiveTab(1);

        tabs.addListener((o,n)->{
            Console.info("Changed to " + n.getTitle());
        });

        Object o = DOM.getElementsByTagName(Document.get(), "body")[0];

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


        new Main(1).doit();
        new Main(2).doit();
        new Main(3).doit();


        //Console.info("Timer setted! " + id);


        /*
        API api = new API((s)->{
            Console.info("CONNECTED!");
        });

        Button b1 = new Button("Send 1");
        Button b2 = new Button("Send 2");

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
        */

    }

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
        long id = Script.setTimeout(1000, t);
    }

    public class SubClass {
        void tt() {
            Console.info("Hello from SUBCLASS!");
            Console.info("aaa=" + Main.this.aaa);
        }
    }
}
