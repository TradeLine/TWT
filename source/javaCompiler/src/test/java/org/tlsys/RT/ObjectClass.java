package org.tlsys.RT;

import org.tlsys.annotations.RuntimeClassName;
import org.tlsys.twt.members.TClassLoader;

@RuntimeClassName(Object.class)
public class ObjectClass extends TestClass {
    private static final long serialVersionUID = 4726834464947516342L;

    public ObjectClass(TClassLoader classLoader) {
        super(classLoader, "TObject", "java.lang.Object", null);
    }
}
