package org.tlsys;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class Main {
    public static void main() {
        Script.code("console.info('Hello from Console')");
    }
}
