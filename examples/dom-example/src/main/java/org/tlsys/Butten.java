package org.tlsys;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.DomNode;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.dom.DOM;

@JSClass
@DomNode("button")
public class Butten {
    public void setTitle(String text) {
        DOM.setHTML(this, text);
    }

    public String getTitle() {
        return DOM.getHTML(this);
    }

    public Butten(String text) {
        Script.code("console.dir(arguments)");
        setTitle(text);
    }

    public Butten() {
        this("NONE");
    }

    public boolean isEnabled() {
        return DOM.getAttribute(this,"disabled") != null;
    }

    public void setEnabled() {
        DOM.setAttribute(this, "disabled","disabled");
    }
}
