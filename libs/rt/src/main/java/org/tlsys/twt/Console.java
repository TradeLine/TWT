package org.tlsys.twt;

import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
public class Console {
    public static void info(String text) {
        Script.code("console.info(",text,")");
    }

    public static void error(String text) {
        Script.code("console.error(",text,")");
    }

    public static void dir(Object text) {
        Script.code("console.dir(",text,")");
    }
}
