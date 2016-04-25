package org.tlsys.twt.members;

import org.tlsys.twt.expressions.AnntationItem;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class VPackage implements VMember, Named {
    private static final long serialVersionUID = -231494842065847334L;
    private final String name;
    private final VPackage parent;
    private final ArrayList<VMember> members = new ArrayList<>();

    public VPackage(String name, VPackage parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getSimpleName() {
        return name;
    }

    @Override
    public String getName() {
        VMember m = getParent();
        if (m instanceof VPackage) {
            if (((VPackage) m).getName() == null)
                return getSimpleName();
            return ((VPackage) m).getName() + "." + getSimpleName();
        }

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

    @Override
    public List<AnntationItem> getList() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Optional<AnntationItem> getByClass(VClass clazz) {
        return Optional.empty();
    }
}
