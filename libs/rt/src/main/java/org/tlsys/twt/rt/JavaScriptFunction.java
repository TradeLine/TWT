package org.tlsys.twt.rt;

import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ReplaceClass(NativeCodeGenerator.class)
public interface JavaScriptFunction {
    public Object call(Object self, Object ... arguments);
    public Object callNew(Object ... arguments);
}
