package org.tlsys;

import org.tlsys.twt.annotations.DomNode;
import org.tlsys.twt.dom.DOM;

@DomNode("button")
public class Button {
    public Button(String title) {
        DOM.setHTML(this, title);
    }
}
