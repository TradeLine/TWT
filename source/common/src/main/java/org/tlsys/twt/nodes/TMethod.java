package org.tlsys.twt.nodes;

import org.tlsys.twt.nodes.code.CodeBlock;

public class TMethod {
    private final String name;
    private final ClassReferance[] arguments;
    private TClass parent;
    private final int modificators;
    private final CodeBlock block;

    public TMethod(String name, ClassReferance[] arguments, int modificators, CodeBlock block) {
        this.name = name;
        this.arguments = arguments;
        this.modificators = modificators;
        this.block = block;
    }

    public String getName() {
        return name;
    }

    public ClassReferance[] getArguments() {
        return arguments;
    }

    public TClass getParent() {
        return parent;
    }

    public void setParent(TClass parent) {
        this.parent = parent;
    }

    public int getModificators() {
        return modificators;
    }
}
