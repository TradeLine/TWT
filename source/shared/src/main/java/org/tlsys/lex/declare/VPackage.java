package org.tlsys.lex.declare;

import org.tlsys.lex.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class VPackage implements Context, Serializable {

    private final String simpleName;

    private final VPackage parent;

    private final List<Context> childs = new ArrayList<>();

    public VPackage(String simpleName, VPackage parent) {
        this.simpleName = simpleName;
        this.parent = parent;

        if (parent != null)
            parent.childs.add(this);
    }

    public void addChild(Context clazz) {
        childs.add(clazz);
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getName() {
        if (parent == null)
            return getSimpleName();
        return (parent.getName() != null?parent.getName()+".":"") + getSimpleName();
    }

    public Optional<VPackage> getPackage(String name) {
        Objects.requireNonNull(name);
        if (name.contains("."))
            throw new IllegalArgumentException("Name of package contains \".\"");

        for (Context c : childs) {
            if (c instanceof VPackage) {
                VPackage p = (VPackage) c;
                if (name.equals(p.getSimpleName()))
                    return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        if (parent != null)
            return parent.find(name, searchIn);
        return Optional.empty();
    }
}
