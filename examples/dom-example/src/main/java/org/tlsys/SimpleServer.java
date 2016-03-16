package org.tlsys;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/hello")
public class SimpleServer {
    @OnMessage
    public String hello(String message, Session session) throws IOException {
        System.out.println("Received : "+ message + " from " + session.getId());
        session.getBasicRemote().sendText(message);
        return "that was echo";
    }

    @OnOpen
    public void myOnOpen(Session session) {
        System.out.println("WebSocket opened: " + session.getId());
    }

    @OnClose
    public void myOnClose(CloseReason reason) {
        System.out.println("Closing a WebSocket due to " + reason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
    }
}
