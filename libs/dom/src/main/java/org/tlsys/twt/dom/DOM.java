package org.tlsys.twt.dom;

import org.tlsys.twt.JArray;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

import java.util.Objects;

@JSClass
public final class DOM {
    private DOM() {
    }

    public static void setAttribute(Object element, String name, String value) {
        Script.code(element,".setAttribute(",name,",",value,")");
    }

    public static Object[] childNodes(Object element) {
        JArray ar = new JArray();
        ar.setJSArray(Script.code(element,".childNodes"));

        Object[] out = new Object[ar.length()];
        for (int i = 0; i < out.length; i++) {
            out[i] = ar.get(i);
        }
        return out;
    }


    public static boolean hasAttribute(Object element, String name) {
        return Script.code(element,".hasAttribute(",name,")");
    }

    public static String getAttribute(Object element, String name) {
        String s = Script.code(element,".getAttribute(",name,")");
        if (s == null || Script.isUndefined(s))
            return null;
        return s;
    }

    public static String removeAttribute(Object element, String name) {
        String s = getAttribute(element, name);
        Script.code(element,".removeAttribute(",name,")");
        return s;
    }

    public static void appendChild(Object element, Object child) {
        Script.code(element,".appendChild(",child,")");
    }

    public static void appendChildBefor(Object before, Object child) {
        Object parent = Objects.requireNonNull(getParent(before));
        Script.code(parent,".insertBefore(",child,",",before,")");
    }

    public static void appendChildAfter(Object after, Object child) {
        Object parent = getParent(after);
        Object[] elements = childNodes(parent);

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

    public static void replaceChild(Object element, Object replaceTo) {
        Object parent = getParent(element);
        Script.code(parent,".replaceChild(",element,",",replaceTo,")");
    }

    public static int childLength(Object element) {
        return Script.code(element,".childNodes.length");
    }

    public static Object getChild(Object element, int index) {
        //TODO добавитиь проверку index: не выходит лион за пределы
        return Script.code(element,".childNodes.item(",index,")");
    }

    public static void removeChild(Object element) {
        Object parent = getParent(element);
        if (parent == null)
            return;
        Script.code(parent,".removeChild(",element,")");
    }

    public static void clearChildOfNode(Object element) {
        for (Object o : childNodes(element)) {
            removeChild(o);
        }
    }

    public static Object[] getElementsByTagName(Object element, String tag) {
        JArray ar = new JArray();
        ar.setJSArray(Script.code(element,".getElementsByTagName(",tag,")"));

        Object[] out = new Object[ar.length()];
        for (int i = 0; i < out.length; i++) {
            out[i] = ar.get(i);
        }
        return out;
    }

    public static void setHTML(Object dom, String html) {
        Script.code(dom,".innerHTML=",html);
    }
    public static String getHTML(Object dom) {
        return Script.code(dom,".innerHTML");
    }

    public static Object getParent(Object element) {
        Object o = Script.code(element,".parentElement");
        if (o == null || Script.isUndefined(o))
            return null;
        return null;
    }

    public static Object queryFirst(Object element, String html) {
        Object dom = Script.code(element,".querySelector(",html,")");
        if (dom == null || Script.isUndefined(dom))
            return null;
        return dom;
    }

    public static Object[] query(Object element, Object selector) {
        JArray ar = new JArray();
        ar.setJSArray(Script.code(element,".querySelectorAll(",selector,")"));

        Object[] out = new Object[ar.length()];
        for (int i = 0; i < out.length; i++) {
            out[i] = ar.get(i);
        }
        return out;
    }
}
