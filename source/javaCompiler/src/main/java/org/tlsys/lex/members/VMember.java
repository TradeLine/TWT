package org.tlsys.lex.members;

import org.tlsys.lex.TNode;

import java.util.Optional;
import java.util.function.Predicate;

public interface VMember extends TNode {
    public int getModifiers();
    public boolean add(VMember member);
    public boolean remove(VMember member);
    public <T extends VMember> Optional<T> getChild(Predicate<VMember> predicate);

    @Override
    public VMember getParent();
}
