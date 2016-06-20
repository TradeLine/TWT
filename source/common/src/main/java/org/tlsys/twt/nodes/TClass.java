package org.tlsys.twt.nodes;

public abstract class TClass {

    private final TMethod[] methods;
    private final SimpleClassReferance superClass;
    private final SimpleClassReferance[] implementClasses;

    public TClass(TMethod[] methods, SimpleClassReferance superClass, SimpleClassReferance[] implementClasses) {
        this.methods = methods;
        this.superClass = superClass;
        this.implementClasses = implementClasses;
    }

    public abstract ClassReferance asReferance();

    public abstract String getName();

    public TMethod[] getMethods() {
        return methods;
    }

    public SimpleClassReferance getSuperClass() {
        return superClass;
    }

    public SimpleClassReferance[] getImplementClasses() {
        return implementClasses;
    }
}
