package org.tlsys;

import org.tlsys.twt.JDictionary;
import org.tlsys.twt.json.Json;


public class API {
    final JDictionary<ResponceListener> outs = new JDictionary<>();
    private SocketAPI s;
    public API(SocketAPI.ConnectListener connectListener) {
        s = new SocketAPI(this, connectListener);
    }

    public void call(ResponceListener listener, String body) {
        Request rc = new Request(body);
        outs.set(rc.getId(), listener);
        String json = Json.toJSON(rc);
        s.send(json);
    }
}
