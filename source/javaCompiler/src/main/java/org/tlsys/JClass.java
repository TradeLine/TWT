package org.tlsys;

import com.github.javaparser.ast.body.TypeDeclaration;
import org.tlsys.lex.members.VClass;
import org.tlsys.lex.members.VMember;

import java.util.Optional;
import java.util.function.Predicate;

public class JClass implements VClass {

    private transient TypeDeclaration typeDeclaration;
    private final VMember parent;

    public JClass(TypeDeclaration typeDeclaration, VMember parent) {
        this.typeDeclaration = typeDeclaration;
        this.parent = parent;
        parent.add(this);
    }

    @Override
    public Optional<VClass> getClass(String name) {
        return null;
    }

    @Override
    public boolean add(VMember member) {
        return false;
    }

    @Override
    public boolean remove(VMember member) {
        return false;
    }

    @Override
    public <T extends VMember> Optional<T> getChild(Predicate<VMember> predicate) {
        return null;
    }
}
