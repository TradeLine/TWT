package org.tlsys.twt;

import org.tlsys.twt.annotations.InvokeGen;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.NotCompile;

@JSClass
@NotCompile
public class Script {

    @InvokeGen(org.tlsys.twt.ScriptInvokeGenerator.class)
    public native static <T> T code(Object ... args);

    @InvokeGen(org.tlsys.twt.ScriptInvokeGenerator.class)
    public native static boolean isUndefined(Object args);

    @InvokeGen(org.tlsys.twt.ScriptInvokeGenerator.class)
    public native static String typeOf(Object args);

    @InvokeGen(org.tlsys.twt.ScriptInvokeGenerator.class)
    public native static boolean isPrototypeOf(Object args, Class clazz);

    @InvokeGen(org.tlsys.twt.ScriptInvokeGenerator.class)
    public static boolean hasOwnProperty(Object object, String name) {
        return CastUtil.toBoolean(Script.code(object,".hasOwnProperty(",name,")"));
    }

    public void tt(){}

    private Script gg;

    public void alert(String text) {
        this.tt();

        gg.gg.gg.tt();

        Script.code("alert(",text,")");
    }

    public interface TimeoutCallback {
        void onTimeout();
    }

    @InvokeGen(org.tlsys.twt.ScriptInvokeGenerator.class)
    public static native long setTimeout(int timeOut, TimeoutCallback callback);

    @InvokeGen(org.tlsys.twt.ScriptInvokeGenerator.class)
    public static native long setInterval(int timeOut, TimeoutCallback callback);

    @InvokeGen(org.tlsys.twt.ScriptInvokeGenerator.class)
    public static native long requestAnimationFrame(FrameRequest callback);

    public static interface FrameRequest {
        public void onFrame(double time);
    }
}
