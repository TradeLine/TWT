package org.tlsys.twt.net;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Console;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.ForceInject;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.events.Events;
import org.tlsys.twt.rt.java.lang.TThrowable;

@JSClass
public abstract class WebSocket {
    private Object js;
    public WebSocket(String url) {
        Console.info("Created for " + url);
        js = Script.code("new WebSocket(",url,")");

        Events.addEventListener(js, "open", (s, e)->onOpenEvent(),false);
        Events.addEventListener(js, "close", (s,e)->onCloseEvent(e),false);
        Events.addEventListener(js, "message", (s,e)->onMessageEvent(e),false);
        Events.addEventListener(js, "error", (s,e)->onErrorEvent(e),false);

        /*
        Script.code(js,".onopen=function(){",self.onOpenEvent(),"}");
        Script.code(js,".onclose=function(e){",self.onCloseEvent(Script.code("e")),"}");
        Script.code(js,".onmessage=function(e){",self.onMessageEvent(Script.code("e")),"}");
        Script.code(js,".onerror=function(e){",self.onErrorEvent(Script.code("e")),"}");
        */
    }

    private boolean onMessageEvent(Object event) {
        onMessage(Script.code(event,".data"));
        return true;
    }

    private boolean onCloseEvent(Object event) {
        CloseEvent e = new CloseEvent(Script.code(event,".reason"),CastUtil.toInt(Script.code(event,".code")),CastUtil.toBoolean(Script.code(event,".wasClean")));
        onClose(e);
        return true;
    }

    private boolean onOpenEvent() {
        Console.info("Connected");
        onOpen();
        return true;
    }

    private boolean onErrorEvent(Object event) {
        onError(CastUtil.cast(TThrowable.jsErrorConvert(event)));
        return true;
    }

    @ForceInject
    protected abstract void onMessage(String data);

    @ForceInject
    protected abstract void onClose(CloseEvent closeEvent);

    @ForceInject
    protected abstract void onOpen();

    @ForceInject
    protected abstract void onError(Throwable throwable);

    public void send(String data) {
        Script.code(js,".send(",data,")");
    }

    public void close(int code, String reason) {
        if (reason != null && reason.length() > 123)
            throw new IllegalArgumentException("reason argument length must be <123");
        Script.code(js, ".close(", CastUtil.toObject(code), ",", reason, ")");
    }

    public State getState() {
        int st = CastUtil.toInt(Script.code(js, ".readyState"));
        Console.info("State:");
        Console.dir(CastUtil.toObject(st));
        Console.dir(Script.code(js, ".readyState"));
        switch (st) {
            case 0: return State.CONNECTING;
            case 1: return State.OPEN;
            case 2: return State.CLOSING;
            case 3: return State.CLOSED;
            default:
                throw new RuntimeException("Unknown state " + st);
        }
    }



    //readyState
    public static enum State {
        CONNECTING,
        OPEN,
        CLOSING,
        CLOSED
    }

    public static class CloseEvent {
        private final String reason;
        private final int code;
        private final boolean wasClean;

        public CloseEvent(String reason, int code, boolean wasClean) {
            this.reason = reason;
            this.code = code;
            this.wasClean = wasClean;
        }

        public String getReason() {
            return reason;
        }

        public int getCode() {
            return code;
        }

        public boolean isWasClean() {
            return wasClean;
        }
    }
}
