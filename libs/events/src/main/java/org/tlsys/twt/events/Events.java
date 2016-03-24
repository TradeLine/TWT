package org.tlsys.twt.events;

import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class Events {
    public enum Type {
        FOCUS,
        BLUR,

        CLICK,
        DOUBLE_CLICK,

        MOUSE_DOWN,
        MOUSE_UP,
        MOUSE_MOVE,
        MOUSE_OVER,

        KEY_DOWN,
        KEY_UP,
        KEY_PRESS;

        private String toEventName() {
            switch (this) {
                case FOCUS:
                    return "focus";
                case BLUR:
                    return "blur";
                case CLICK:
                    return "click";
                case DOUBLE_CLICK:
                    return "dblclick";
                case MOUSE_DOWN:
                    return "mousedown";
                case MOUSE_UP:
                    return "mouseup";
                case MOUSE_MOVE:
                    return "mousemove";
                case MOUSE_OVER:
                    return "mouseover";
                case KEY_DOWN:
                    return "keydown";
                case KEY_UP:
                    return "keyup";
                case KEY_PRESS:
                    return "keypress";
                default:
                    throw new RuntimeException("Unknown type");
            }
        }
    }

    @CodeGenerator(EventCodeGenerator.class)
    public static void addEventListener(Object object, String eventType, EventListener listener, boolean useCapture) {
        listener.onEvent(listener, null);
    }

    public static void addEventListener(Object object, String eventType, EventListener listener) {
        addEventListener(object, eventType, listener, false);
    }

    @CodeGenerator(EventCodeGenerator.class)
    public static void removeEventListener(Object object, String eventType, EventListener listener, boolean useCapture) {
        listener.onEvent(listener, null);
    }

    public static void removeEventListener(Object object, String eventType, EventListener listener) {
        removeEventListener(object, eventType, listener, false);
    }

    @JSClass
    @FunctionalInterface
    public interface EventListener {
        public void onEvent(Object sender, Object event);
    }
}
