package org.tlsys.twt.nodes;

import java.io.Serializable;

public class MethodReferance {
    private final ClassReferance classRef;
    private final String name;
    private final String signature;

    public MethodReferance(ClassReferance classRef, String name, String signature) {
        this.classRef = classRef;
        this.name = name;
        this.signature = signature;
    }

    public ClassReferance getClassRef() {
        return classRef;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }
}
