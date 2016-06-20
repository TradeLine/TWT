package org.tlsys.twt;

import org.tlsys.twt.nodes.TClass;

import java.io.IOException;

public interface ClassGenerator {
    public void generate(TClass clazz, Appendable text, LineMarker marker) throws IOException;
}
