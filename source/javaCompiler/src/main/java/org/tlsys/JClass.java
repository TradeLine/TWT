package org.tlsys;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.tlsys.lex.members.VClass;
import org.tlsys.lex.members.VMember;
import org.tlsys.lex.members.VMethod;

import java.util.Optional;
import java.util.function.Predicate;

public class JClass implements VClass {

    private final VMember parent;
    private final int modifiers;
    private transient TypeDeclaration typeDeclaration;

    public JClass(TypeDeclaration typeDeclaration, VMember parent) {
        this.typeDeclaration = typeDeclaration;
        this.parent = parent;
        parent.add(this);
        modifiers = typeDeclaration.getModifiers();
    }

    @Override
    public Optional<VClass> getClass(String name) {
        return null;
    }

    @Override
    public Optional<VMethod> findMethod(String name, ArgumentRequid... requids) {
        for (BodyDeclaration bd : typeDeclaration.getMembers()) {
            if (bd instanceof MethodDeclaration) {
                MethodDeclaration md = (MethodDeclaration) bd;
                if (!md.getName().equals(name))
                    continue;
            }
        }
        return null;
    }

    @Override
    public int getModifiers() {
        return modifiers;
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
