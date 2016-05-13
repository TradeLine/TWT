package org.tlsys.RT;

import org.tlsys.twt.members.TClassLoader;

public class IntClass extends TestClass {
    private static final long serialVersionUID = -1572028616098928558L;

    public IntClass(TClassLoader classLoader) {
        super(classLoader, "Tint", "int", classLoader.findClassByName(Object.class.getName()).get().asRef());
    }
}
