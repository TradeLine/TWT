package org.tlsys.lex.members;

import java.util.Optional;
import java.util.function.Predicate;

public interface VMember {
    public int getModifiers();
    public boolean add(VMember member);
    public boolean remove(VMember member);
    public <T extends VMember> Optional<T> getChild(Predicate<VMember> predicate);

    public VMember getParent();
}
