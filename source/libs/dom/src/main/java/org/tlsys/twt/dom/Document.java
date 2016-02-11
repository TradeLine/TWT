package org.tlsys.twt.dom;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public final class Document {
    private Document() {
        throw new IllegalStateException("Document can't be created");
    }

    public static Document get() {
        return Script.code("document");
    }
}
