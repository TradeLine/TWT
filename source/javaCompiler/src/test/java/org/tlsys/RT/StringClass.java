package org.tlsys.RT;

import org.tlsys.twt.members.TClassLoader;

public class StringClass extends TestClass {
    private static final long serialVersionUID = -1572028616098928558L;

    public StringClass(TClassLoader classLoader) {
        super(classLoader, "TString", "java.lang.String", classLoader.findClassByName(Object.class.getName()).get());
    }
}