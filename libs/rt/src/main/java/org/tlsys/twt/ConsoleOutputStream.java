package org.tlsys.twt;

import org.tlsys.twt.annotations.JSClass;

import java.io.IOException;
import java.io.OutputStream;

@JSClass
public class ConsoleOutputStream extends OutputStream {

    private String content;
    private final Target target;

    public ConsoleOutputStream(Target target) {
        this.target = target;
    }

    @Override
    public void write(int b) throws IOException {
        content+=(char)b;
    }

    @Override
    public void flush() throws IOException {
        target.printLine(content);
    }

    public static interface Target {
        public void printLine(String text);
    }

    public static final Target INFO = (text)->{
            Script.code("console.info(",text,")");
    };

    public static final Target ERROR = (text)->{
        Script.code("console.error(",text,")");
    };

    public static final Target DEBUG = (text)->{
        Script.code("console.debug(",text,")");
    };
}
