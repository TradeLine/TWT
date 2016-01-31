package org.tlsys.twt;

import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.MethodBodyGen;

//@JSClass
public class Event {
    @CodeGenerator(org.tlsys.twt.EventMethodBody.class)
    public native static void setEvent(Object object, String eventName, EventListener listener);

    @CodeGenerator(org.tlsys.twt.EventMethodBody.class)
    public native static EventListener getEvent(Object object, String eventName);

    @CodeGenerator(org.tlsys.twt.EventMethodBody.class)
    public native static void addEventListener(Object object, String eventName, EventListener eventListener, boolean useCapture);

    @CodeGenerator(org.tlsys.twt.EventMethodBody.class)
    public native static void removeEventListener(Object object, String eventName, EventListener eventListener, boolean useCapture);

    public static void addEventListener(Object object, String eventName, EventListener eventListener) {
        addEventListener(object, eventName, eventListener, false);
    }
}
