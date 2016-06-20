package org.tlsys.twt;

import org.tlsys.twt.nodes.TMethod;

import java.io.IOException;
import java.lang.reflect.Modifier;

public class NativeMethodCodeGenerator implements MethodCodeGenerator {
    private final NativeClassCodeGenerator classCodeGenerator;

    public NativeMethodCodeGenerator(NativeClassCodeGenerator classCodeGenerator) {
        this.classCodeGenerator = classCodeGenerator;
    }

    @Override
    public void generate(TMethod tMethod, Appendable o, LineMarker lineMarker) throws IOException {
        o.append(tMethod.getParent().getName()).append(".");
        if (!Modifier.isStatic(tMethod.getModificators())){
            o.append("prototype.");
        }
        o.append(tMethod.getName());
        o.append("=function(){");
        o.append("}");

    }
}
