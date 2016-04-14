package org.tlsys;

import org.tlsys.twt.annotations.JSClass;

@JSClass
public class Main {

    /*
    private static InitClass stat = new InitClass("STATIC FIELD");
    private int aaa;
    private InitClass non_stat = new InitClass("NONSTATIC FIELD");
    */

/*
    static {
        Console.info("HELLO FROM STATIC BLOCK");
    }

    public Main(int aaab, int bbb) {
        Console.info("???");
        //aaa = aaab;

        Function<Void, Void> f = (a) -> {
            Console.info("Hello from LAMBDA IN CLASS " + this);
            return null;
        };

        f.apply(null);
    }
*/
    public static void giveException() {
        throw new RuntimeException("ERROR!");
    }


    public static int get(int abc, int def) {
        int a = abc;
        return 0;
    }

    public static void main() {
        int a = 10;
        int b = 11;
        int c = get(1, 2);
        giveException();
        return;
    }
}
