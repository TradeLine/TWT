package org.tlsys.twt.dependency;

import org.tlsys.twt.nodes.MethodReferance;

public class MethodDependency extends Dependency {
    private final MethodReferance method;

    public MethodDependency(MethodReferance method) {
        this.method = method;
    }
}
