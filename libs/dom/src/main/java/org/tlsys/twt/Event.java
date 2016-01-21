package org.tlsys.twt;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.MethodBodyGen;

@JSClass
public class Event {
    @MethodBodyGen("org.tlsys.twt.EventMethodBody")
    public native static void setEvent(Object object, String eventName, EventListener listener);

    @MethodBodyGen("org.tlsys.twt.EventMethodBody")
    public native static EventListener getEvent(Object object, String eventName);

    @MethodBodyGen("org.tlsys.twt.EventMethodBody")
    public native static void addEventListener(Object object, String eventName, EventListener eventListener, boolean useCapture);

    @MethodBodyGen("org.tlsys.twt.EventMethodBody")
    public native static void removeEventListener(Object object, String eventName, EventListener eventListener, boolean useCapture);

    public static void addEventListener(Object object, String eventName, EventListener eventListener) {
        addEventListener(object, eventName, eventListener, false);
    }
}
