package org.tlsys;

import org.tlsys.twt.annotations.ForceInject;
import org.tlsys.twt.annotations.JSClass;

@ForceInject
@JSClass
public class Response {
    public int[] values;
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
