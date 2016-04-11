package org.tlsys.lex.members;

import java.util.Optional;
import java.util.function.Predicate;

public interface VMember {
    public boolean add(VMember member);
    public boolean remove(VMember member);
    public <T extends VMember> Optional<T> getChild(Predicate<VMember> predicate);
}
