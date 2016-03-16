package org.tlsys;

import org.tlsys.twt.Console;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.events.WebSocket;

@JSClass
public class SimpleClient extends WebSocket {
    public SimpleClient() {
        super("ws://tlsys.org:8080/dom-example/hello");
    }

    @Override
    protected void onMessage(String data) {
        Console.info(">>MESSAGE>>" + data);
    }

    @Override
    protected void onClose(CloseEvent closeEvent) {
        Console.info(">>CLOSE");
    }

    @Override
    protected void onOpen() {
        Console.info(">>OPEN");
    }

    @Override
    protected void onError(Throwable throwable) {
        Console.info(">>ERROR");
    }
}
