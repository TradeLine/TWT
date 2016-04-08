package org.tlsys;

import org.tlsys.twt.Console;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class SecondApi {

    private final API api;
    private int id;

    public SecondApi(API api, int id) {
        this.api = api;
        this.id = id;
    }

    public void send(String data) {
        api.call(s->{
            Console.info("Recived " + data + ", to " + id);
        }, data);
    }
}
