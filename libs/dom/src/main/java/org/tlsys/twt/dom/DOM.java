package org.tlsys.twt.dom;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.JArray;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

import java.util.Objects;

@JSClass
public final class DOM {
    private DOM() {
    }

    public static void setAttribute(Object element, String name, String value) {
        Objects.requireNonNull(element, "Element is NULL");
        if (value == null) {
            removeAttribute(element, name);
            return;
        }
        Script.code(element,".setAttribute(",name,",",value,")");
    }

    public static Object[] childNodes(Object element) {
        return JArray.fromJSArray(Script.code(element,".childNodes"), Object.class);
    }


    public static boolean hasAttribute(Object element, String name) {
        return Script.code(element,".hasAttribute(",name,")");
    }

    public static int getOffsetWidth(Object element) {
        Elements.requireElement(element);
        return CastUtil.toInt(Script.code(element,".offsetWidth"));
    }

    public static int getOffsetHeight(Object element) {
        Elements.requireElement(element);
        return CastUtil.toInt(Script.code(element,".offsetHeight"));
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

    public static <T> T appendChild(Object element, T child) {
        //Elements.requireElement(child, "Child must be Node");//FIXME исправить
        Script.code(element,".appendChild(",child,")");
        return child;
    }

    public static <T> T appendChildBefor(Object before, T child) {
        Object parent = Objects.requireNonNull(getParent(before));
        Script.code(parent,".insertBefore(",child,",",before,")");
        return child;
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
        return CastUtil.toInt(Script.code(element, ".childNodes.length"));
    }

    public static Object getChild(Object element, int index) {
        Objects.requireNonNull(element, "Element is NULL");
        //TODO добавитиь проверку index: не выходит за пределы
        return Script.code(element,".childNodes.item(", CastUtil.toObject(index),")");
    }

    public static void removeChild(Object element) {
        Objects.requireNonNull(element, "Element is NULL");
        Object parent = getParent(element);
        if (parent == null)
            return;
        Script.code(parent,".removeChild(",element,")");
    }

    public static void clearChildOfNode(Object element) {
        Objects.requireNonNull(element, "Element is NULL");
        for (Object o : childNodes(element)) {
            removeChild(o);
        }
    }

    public static Object[] getElementsByTagName(Object element, String tag) {
        Objects.requireNonNull(element, "Element is NULL");
        Objects.requireNonNull(tag, "Tag is NULL");
        JArray ar = new JArray();
        ar.setJSArray(Script.code(element,".getElementsByTagName(",tag,")"));

        Object[] out = new Object[ar.length()];
        for (int i = 0; i < out.length; i++) {
            out[i] = ar.get(i);
        }
        return out;
    }

    public static void setHTML(Object element, String html) {
        Objects.requireNonNull(element, "Element is NULL");
        if (html == null)
            html = "";
        Script.code(element,".innerHTML=",html);
    }
    public static String getHTML(Object element) {
        Objects.requireNonNull(element, "Element is NULL");
        return Script.code(element,".innerHTML");
    }

    public static Object getParent(Object element) {
        Object o = Script.code(element,".parentElement");
        if (o == null || Script.isUndefined(o))
            return null;
        return o;
    }

    public static Object queryFirst(Object element, String selector) {
        Objects.requireNonNull(element, "Element is NULL");
        Objects.requireNonNull(selector, "Selector is NULL");
        Object dom = Script.code(element,".querySelector(",selector,")");
        if (dom == null || Script.isUndefined(dom))
            return null;
        return dom;
    }

    public static Object[] query(Object element, String selector) {
        Objects.requireNonNull(element, "Element is NULL");
        Objects.requireNonNull(selector, "Selector is NULL");
        JArray ar = new JArray();
        ar.setJSArray(Script.code(element,".querySelectorAll(",selector,")"));

        Object[] out = new Object[ar.length()];
        for (int i = 0; i < out.length; i++) {
            out[i] = ar.get(i);
        }
        return out;
    }

    public static ClassList getCssClassList(Object element) {
        Objects.requireNonNull(element, "Element is NULL");
        Object o = Script.code(element,".classList");
        if (o == null || Script.isUndefined(o))
            return null;
        return new ClassList(o);
    }

    public static DomStyle getStyle(Object element) {
        Objects.requireNonNull(element);
        DomStyle ds = Script.code(element,".TWT_DOM_STYLE");
        if (ds == null || Script.isUndefined(ds)) {
            ds = new DomStyle(Script.code(element, ".style"));
            Script.code(element,".TWT_DOM_STYLE=",ds);
            return ds;
        }
        return ds;
    }
}
