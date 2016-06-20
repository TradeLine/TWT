package org.tlsys.twt;

import org.tlsys.twt.nodes.TMethod;

import java.io.IOException;

public interface MethodCodeGenerator {
    public void generate(TMethod method, Appendable text, LineMarker marker) throws IOException;
}
