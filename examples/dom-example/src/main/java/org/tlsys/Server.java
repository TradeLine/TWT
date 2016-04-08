package org.tlsys;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/api")
public class Server {

    @OnMessage
    public String hello(String message, Session session) throws IOException {
        Request r = (Request)JsonReader.jsonToJava(message);
        Response o = new Response(r.getId(), r.getBody() + " from serevr");
        o.values = new int[5];
        o.values[0] = o.values[1] = o.values[2] = o.values[3] = o.values[4] = 1;
        return JsonWriter.objectToJson(o);
    }
}
