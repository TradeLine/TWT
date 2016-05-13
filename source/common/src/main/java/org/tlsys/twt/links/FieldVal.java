package org.tlsys.twt.links;

public final class FieldVal {
    private final ClassVal classVal;
    private final String name;

    public FieldVal(ClassVal classVal, String name) {
        this.classVal = classVal;
        this.name = name;
    }

    public ClassVal getClassVal() {
        return classVal;
    }

    public String getName() {
        return name;
    }
}
