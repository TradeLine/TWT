package org.tlsys;

import org.tlsys.twt.annotations.ForceInject;


public class Response {
    private int id;
    private String body;

    public Response(int id, String body) {
        this.id = id;
        this.body = body;
    }

    @ForceInject
    public Response() {
    }

    public int getId() {
        return id;
    }

    public String getBody() {
        return body;
    }
}
