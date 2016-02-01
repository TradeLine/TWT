package org.tlsys;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.rt.java.lang.TClass;

@JSClass
public class Main {

    public Main() {
        Script.code("console.info('Class created!')");
        Script.code("console.info('Hash='+",hashCode(),")");
        Test t = new Test();
        Script.code("document.getElementsByTagName('body')[0].appendChild(",t,")");
        //print("Hello","World!");
        TClass cl = CastUtil.cast(String.class);
        Class ar = cl.getArrayClass();

        Script.code("console.dir(",ar,")");
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
