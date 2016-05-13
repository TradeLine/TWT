package org.tlsys.twt;

import org.tlsys.twt.links.ClassVal;

import java.io.Serializable;

public abstract class ExecuteVal implements Serializable {
    private static final long serialVersionUID = 7277112262998007011L;
    private final ClassVal classVal;
    private final ClassVal[] arguments;

    public ExecuteVal(ClassVal classVal, ClassVal[] arguments) {
        this.classVal = classVal;
        this.arguments = arguments;
    }

    public ClassVal getParent() {
        return classVal;
    }

    public ClassVal[] getArguments() {
        return arguments;
    }
}
