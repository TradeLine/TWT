package org.tlsys.twt;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.dom.DomElement;

@JSClass
public final class Style {
    private Style() {
    }

    public static int fromPx(String text) {
        return Integer.parseInt(text.substring(0, text.lastIndexOf("px")));
    }

    public static String toPx(int value) {
        return Integer.toString(value) + "px";
    }

    public static int widthPx(DomElement element) {
        return fromPx(Script.code(element, ".style.width"));
    }

    public static int heightPx(DomElement element) {
        return fromPx(Script.code(element,"style.height"));
    }

    public static void widthPx(DomElement element, int width) {
        Script.code(element, ".style.width=",toPx(width));
    }

    public static void heightPx(DomElement element, int height) {
        Script.code(element, ".style.height=",toPx(height));
    }

    public static void topPx(DomElement element, int top) {
        Script.code(element, ".style.top=",toPx(top));
    }

    public static void leftPx(DomElement element, int left) {
        Script.code(element, ".style.left=",toPx(left));
    }

    public static int topPx(DomElement element) {
        return fromPx(Script.code(element,".style.top"));
    }

    public static int leftPx(DomElement element) {
        return fromPx(Script.code(element,".style.left"));
    }
}
