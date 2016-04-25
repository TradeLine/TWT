package org.tlsys.twt.members;

import org.tlsys.twt.TNode;

import java.util.Optional;
import java.util.function.Predicate;

public interface VMember extends TNode, Annotated {
    public int getModifiers();
    public boolean add(VMember member);
    public boolean remove(VMember member);
    public <T extends VMember> Optional<T> getChild(Predicate<VMember> predicate);

    @Override
    public VMember getParent();
}
