package org.tlsys;

import org.tlsys.twt.annotations.JSClass;

@JSClass
public class Request {
    private int id;
    private String body;

    private static int ids = 0;

    public Request(String body) {
        this.id = ids++;
        this.body = body;
    }

    public Request() {
    }

    public int getId() {
        return id;
    }

    public String getBody() {
        return body;
    }
}
