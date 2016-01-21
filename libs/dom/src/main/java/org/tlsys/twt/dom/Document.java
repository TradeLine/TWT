package org.tlsys.twt.dom;

import org.tlsys.twt.annotations.InvokeGen;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class Document implements DomElement {
    private Document() {
        throw new IllegalStateException("Document can't be created");
    }

    @InvokeGen("org.tlsys.twt.dom.DocumentInvoke")
    public native static Document get();

    @InvokeGen("org.tlsys.twt.dom.DocumentInvoke")
    public native DomElement createElement(String tagName);

    @InvokeGen("org.tlsys.twt.dom.DocumentInvoke")
    public native DomElement getElementById(String id);
}
