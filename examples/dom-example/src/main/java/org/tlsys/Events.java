package org.tlsys;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class Events {
    public enum Type {
        CLICK,
        MOUSE_DOWN,
        MOUSE_UP,
        MOUSE_MOVE;
    }

    @CodeGenerator(EventCodeGenerator.class)
    public static void addEventListener(Object object, String eventType, EventListener listener, boolean useCapture) {
    }

    @CodeGenerator(EventCodeGenerator.class)
    public static void removeEventListener(Object object, String eventType, EventListener listener, boolean useCapture) {
    }

    @JSClass
    @FunctionalInterface
    public interface EventListener {
        public void onEvent(Object sender, Object event);
    }
}
