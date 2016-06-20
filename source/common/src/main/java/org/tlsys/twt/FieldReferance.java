package org.tlsys.twt;

import org.tlsys.twt.nodes.ClassReferance;

public class FieldReferance {
    private final ClassReferance classReferance;
    private final String name;

    public FieldReferance(ClassReferance classReferance, String name) {
        this.classReferance = classReferance;
        this.name = name;
    }

    public ClassReferance getClassReferance() {
        return classReferance;
    }

    public String getName() {
        return name;
    }
}
