package org.tlsys.RT;

import org.tlsys.twt.members.TClassLoader;

public class BooleanClass extends TestClass {
    private static final long serialVersionUID = -1572028616098928558L;

    public BooleanClass(TClassLoader classLoader) {
        super(classLoader, "Tboolean", "boolean", classLoader.findClassByName(Object.class.getName()).get().asRef());
    }
}
