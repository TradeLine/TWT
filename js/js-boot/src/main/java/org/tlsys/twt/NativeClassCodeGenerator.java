package org.tlsys.twt;

import org.tlsys.twt.nodes.TClass;
import org.tlsys.twt.nodes.TMethod;

import java.io.IOException;

public class NativeClassCodeGenerator implements ClassGenerator {
    @Override
    public void generate(TClass clazz, Appendable o, LineMarker lineMarker) throws IOException {
        o.append("var ").append(clazz.getName()).append("=function(){}");
        NativeMethodCodeGenerator mg = new NativeMethodCodeGenerator(this);
        for (TMethod m : clazz.getMethods()) {
            mg.generate(m, o, lineMarker);
        }
    }
}
