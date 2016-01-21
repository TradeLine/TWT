package org.tlsys.twt.dom;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public interface FocusElement extends DomElement {
    public default void focus() {
        Script.code(this,".focus()");
    }

    public default void blur() {
        Script.code(this,".blur()");
    }

    public default void setTabIndex(int index) {
        DOM.setAttribute(this, "tabindex", Integer.toString(index));
    }

    public default void clearTabIndex() {
        DOM.removeAttribute(this, "tabindex");
    }
}
