package org.tlsys;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.classes.ArrayBuilder;
import org.tlsys.twt.rt.java.lang.TClass;

@JSClass
public class Main extends Parent<String> {

    public Main() {
        Script.code("console.info('Class created!')");
        Script.code("console.info('Hash='+",hashCode(),")");
        Test t = new Test();
        Script.code("document.getElementsByTagName('body')[0].appendChild(",t,")");
        //print("Hello","World!");
        TClass cl = CastUtil.cast(String.class);
        Class ar = cl.getArrayClass();

        Script.code("console.dir(",ar,")");

        //String[] strings = ArrayBuilder.create(String.class, "1","2","3","4","5","6","7");
        String[][] strings = {{"1","2","3"},{"4","5","6","7"}};
        String[] str = new String[10];
        Script.code("console.dir(", strings,")");
        Script.code("console.dir(", str,")");
        for (int i = 0; i < strings.length; i++)
            Script.code("console.info('LEN='+", strings[i],")");
        print("Hello!", "World!");
    }

    @Override
    public void doit(String val) {
        Script.code("console.info('Parent::doit')");
        super.doit(val);
    }

    public static void print(String ... list) {
        for (String s : list) {
            Script.code("console.info('-->'+", s,")");
        }
    }

    public static void main() {
        Main m = new Main();
        Script.code("console.info('Hello from Console')");
    }
}
