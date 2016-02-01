package org.tlsys;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class Main {

    public Main() {
        Script.code("console.info('Class created!')");
        Script.code("console.info('Hash='+",hashCode(),")");
        Test t = new Test();
        Script.code("document.getElementsByTagName('body')[0].appendChild(",t,")");
        print("Hello","World!");
    }

    public static void print(String ... list) {
        for (String s : list) {
            System.out.println("->>" + s);
        }
    }

    public static void main() {
        Main m = new Main();
        Script.code("console.info('Hello from Console')");
    }
}
