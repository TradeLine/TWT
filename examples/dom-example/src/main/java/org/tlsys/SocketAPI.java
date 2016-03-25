package org.tlsys;

import org.tlsys.twt.Console;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.json.Json;
import org.tlsys.twt.net.WebSocket;

@JSClass
public class SocketAPI extends WebSocket {

    private API api;

    private final ConnectListener connectListener;

    public SocketAPI(API api, ConnectListener connectListener) {
        super("ws://tlsys.org:8080/dom/api");
        this.api = api;
        this.connectListener = connectListener;
    }

    @Override
    protected void onMessage(String s) {
        Console.info("Getted " + s);
        Response rr = (Response) Json.fromJSON(s);
        Console.info("Search request " + rr.getId());
        ResponceListener rl = api.outs.remove(rr.getId());
        if (rl == null)
            Console.error("request #" + rr.getId() + " not found");
        else {
            Console.info("request #" + rr.getId() + " founeded");
            rl.oo(rr.getBody());
        }
    }

    @Override
    protected void onClose(CloseEvent closeEvent) {
        Console.info(">>CLOSED");
    }

    @Override
    protected void onOpen() {
        if (connectListener != null)
            connectListener.onConnected(this);
    }

    @Override
    protected void onError(Throwable throwable) {
        Console.info(">>ERROR");
    }

    public interface ConnectListener {
        public void onConnected(SocketAPI api);
    }
}
