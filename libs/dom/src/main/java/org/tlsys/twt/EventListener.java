package org.tlsys.twt;

import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.MethodAlias;

//@JSClass
@FunctionalInterface
//@CodeGenerator(NativeCodeGenerator.class)
public interface EventListener {
    public void onEvent(Object sender, Event event);
}
