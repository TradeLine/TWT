package org.tlsys;

import org.tlsys.twt.JDictionary;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.json.Json;

@JSClass
public class API {
    public API(SocketAPI.ConnectListener connectListener) {
        s = new SocketAPI(this, connectListener);
    }

    private SocketAPI s;
    final JDictionary<ResponceListener> outs = new JDictionary<>();

    public void call(ResponceListener listener, String body) {
        Request rc = new Request(body);
        outs.set(rc.getId(), listener);
        String json = Json.toJSON(rc);
        s.send(json);
    }
}
