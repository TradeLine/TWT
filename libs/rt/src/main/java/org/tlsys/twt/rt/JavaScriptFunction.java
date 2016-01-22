package org.tlsys.twt.rt;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;
import org.tlsys.twt.rt.java.lang.NativeCodeGenerator;

@JSClass
@ReplaceClass(NativeCodeGenerator.class)
public interface JavaScriptFunction {
    public Object call(Object self, Object ... arguments);
    public Object callNew(Object ... arguments);
}
