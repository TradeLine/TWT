package org.tlsys.twt.nodes;

public class ArrayClassReferance extends ClassReferance {
    private final ClassReferance ref;

    public ArrayClassReferance(ClassReferance ref) {
        this.ref = ref;
    }

    @Override
    public String getName() {
        return "[" + ref.getName();
    }

    public ClassReferance getRef() {
        return ref;
    }
}
