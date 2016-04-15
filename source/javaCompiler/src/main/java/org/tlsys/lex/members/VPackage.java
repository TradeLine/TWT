package org.tlsys.lex.members;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class VPackage implements VMember {
    private final String name;
    private final VPackage parent;

    private final ArrayList<VMember> members = new ArrayList<>();

    public VPackage(String name, VPackage parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getModifiers() {
        return Modifier.PUBLIC;
    }

    @Override
    public boolean add(VMember member) {
        return members.add(member);
    }

    @Override
    public boolean remove(VMember member) {
        return members.remove(member);
    }

    @Override
    public <T extends VMember> Optional<T> getChild(Predicate<VMember> predicate) {
        for (VMember m : members) {
            if (predicate.test(m))
                return Optional.of((T) m);
        }
        return Optional.empty();
    }

    @Override
    public VMember getParent() {
        return parent;
    }
}
