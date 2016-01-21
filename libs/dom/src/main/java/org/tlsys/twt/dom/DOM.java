package org.tlsys.twt.dom;

import org.tlsys.twt.Event;
import org.tlsys.twt.EventListener;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.InvokeGen;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.NotCompile;

import java.util.Objects;

@JSClass
public final class DOM {
    private DOM() {
    }

    public static void setEvent(DomElement element, EventType eventName, EventListener listener) {
        Event.setEvent(element, eventName.toEventName(), listener);
    }

    public static EventListener getEvent(DomElement element, EventType eventName) {
        return Event.getEvent(element, eventName.toEventName());
    }

    public static void addEventListener(DomElement element, EventType eventName, EventListener listener) {
        addEventListener(element, eventName, listener, false);
    }

    public static void addEventListener(DomElement element, EventType eventName, EventListener listener, boolean useCapture) {
        Event.addEventListener(element, eventName.toEventName(), listener, useCapture);
    }

    public static void removeEventListener(DomElement element, EventType eventName, EventListener listener, boolean useCapture) {
        Event.removeEventListener(element, eventName.toEventName(), listener, useCapture);
    }

    public static void removeEventListener(DomElement element, EventType eventName, EventListener listener) {
        removeEventListener(element, eventName, listener, false);
    }

    @NotCompile
    @InvokeGen("org.tlsys.twt.dom.DomMethodInvokeGenerator")
    public static native void setAttribute(DomElement element, String name, String value);

    @NotCompile
    @InvokeGen("org.tlsys.twt.dom.DomMethodInvokeGenerator")
    public static native DomElement[] childNodes(DomElement element);


    @NotCompile
    @InvokeGen("org.tlsys.twt.dom.DomMethodInvokeGenerator")
    public static native boolean hasAttribute(DomElement element, String name);

    @NotCompile
    @InvokeGen("org.tlsys.twt.dom.DomMethodInvokeGenerator")
    public static native String getAttribute(DomElement element, String name);

    @NotCompile
    @InvokeGen("org.tlsys.twt.dom.DomMethodInvokeGenerator")
    public static native String removeAttribute(DomElement element, String name);

    @NotCompile
    @InvokeGen("org.tlsys.twt.dom.DomMethodInvokeGenerator")
    public static native void appendChild(DomElement element, DomElement child);

    public static void appendChildBefor(DomElement before, DomElement child) {
        DomElement parent = Objects.requireNonNull(getParent(before));
        Script.code(parent,".insertBefore(",child,",",before,")");
    }

    public static void appendChildAfter(DomElement after, DomElement child) {
        DomElement parent = getParent(after);
        DomElement[] elements = childNodes(parent);

        if (elements[elements.length-1] == after) {
            appendChild(parent, child);
            return;
        }

        for (int i = 0; i < elements.length; i++) {
            if (elements[i] == after) {
                if (i == elements.length-1)
                    throw new RuntimeException("before node is last of dom");
                appendChildBefor(elements[i+1], child);
                return;
            }
        }
        throw new RuntimeException("Can not found before node");
    }

    public static void replaceChild(DomElement element, DomElement replaceTo) {
        DomElement parent = getParent(element);
        Script.code(parent,".replaceChild(",element,",",replaceTo,")");
    }

    public static int childLength(DomElement element) {
        return Script.code(element,".childNodes.length");
    }

    public static DomElement getChild(DomElement element, int index) {
        //TODO добавитиь проверку index: не выходит лион за пределы
        return Script.code(element,".childNodes.item(",index,")");
    }

    @NotCompile
    @InvokeGen("org.tlsys.twt.dom.DomMethodInvokeGenerator")
    public static native void removeChild(DomElement element, DomElement child);

    @NotCompile
    @InvokeGen("org.tlsys.twt.dom.DomMethodInvokeGenerator")
    public static native DomElement[] getElementsByTagName(DomElement element, String tag);

    @NotCompile
    @InvokeGen("org.tlsys.twt.dom.DomMethodInvokeGenerator")
    public static native String getHTML(DomElement element);

    @NotCompile
    @InvokeGen("org.tlsys.twt.dom.DomMethodInvokeGenerator")
    public static native void setHTML(DomElement element, String html);

    @NotCompile
    @InvokeGen("org.tlsys.twt.dom.DomMethodInvokeGenerator")
    public static native DomElement getParent(DomElement element);

    public static DomElement queryFirst(DomElement element, String html) {
        DomElement dom = Script.code(element,".querySelector(",html,")");
        if (dom == null || Script.isUndefined(dom))
            return null;
        return dom;
    }

    @NotCompile
    @InvokeGen("org.tlsys.twt.dom.DomMethodInvokeGenerator")
    public static native DomElement[] query(DomElement element, String html);

    @NotCompile
    @InvokeGen("org.tlsys.twt.dom.DomMethodInvokeGenerator")
    public native static <T extends DomElement> T createFor(DomElement element, Class<T> clazz);

    public enum EventType {
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
}
