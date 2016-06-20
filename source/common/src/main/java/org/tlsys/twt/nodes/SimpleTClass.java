package org.tlsys.twt.nodes;

public class SimpleTClass extends TClass {


    private final String name;
    private transient SimpleClassReferance simpleClassReferance;

    public SimpleTClass(String name, TMethod[] methods, SimpleClassReferance superClass, SimpleClassReferance[] implementClasses) {
        super(methods, superClass, implementClasses);
        this.name = name;
    }

    @Override
    public ClassReferance asReferance() {
        if (simpleClassReferance == null)
            simpleClassReferance = new SimpleClassReferance(getName());
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
