package org.tlsys;

import org.tlsys.lex.members.VMember;
import org.tlsys.lex.members.VPackage;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class JavaPackage implements VPackage {

    private final String name;

    private final ArrayList<VMember> members = new ArrayList<>();

    public JavaPackage(String name) {
        this.name = name;
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
            if(predicate.test(m))
                return Optional.of((T)m);
        }
        return Optional.empty();
    }

    @Override
    public String getName() {
        return name;
    }
}
