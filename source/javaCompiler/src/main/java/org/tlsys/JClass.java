package org.tlsys;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.tlsys.lex.members.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class JClass implements VClass {

    private static final long serialVersionUID = 2551751885402816213L;
    private final VMember parent;
    private final int modifiers;
    private final transient TClassLoader classLoader;
    private final ArrayList<VMethod> methods = new ArrayList<>();
    private final ArrayList<org.tlsys.lex.members.ClassModificator> modificators = new ArrayList<>();
    private transient TypeDeclaration typeDeclaration;

    public JClass(TypeDeclaration typeDeclaration, VMember parent, TClassLoader classLoader) {
        this.typeDeclaration = typeDeclaration;
        this.parent = parent;
        this.classLoader = classLoader;
        parent.add(this);
        modifiers = typeDeclaration.getModifiers();
    }

    public void addModificator(org.tlsys.lex.members.ClassModificator mod) {
        modificators.add(mod);
    }

    protected abstract String getSimpleName();

    @Override
    public String getName() {
        VMember m = getParent();
        if (m instanceof VClass)
            return ((VClass) m).getName() + "$" + getSimpleName();

        if (m instanceof VPackage) {
            if (((VPackage) m).getName() == null)
                return getSimpleName();
            return ((VPackage) m).getName() + "." + getSimpleName();
        }

        throw new RuntimeException("Unknown parent");
    }

    @Override
    public Optional<VClass> getClass(String name) {
        return null;
    }

    @Override
    public Optional<VMethod> findMethod(String name, MehtodSearchRequest request) {

        for (BodyDeclaration bd : typeDeclaration.getMembers()) {
            if (bd instanceof MethodDeclaration) {
                MethodDeclaration md = (MethodDeclaration) bd;
                if (!md.getName().equals(name))
                    continue;
                if (md.getParameters().isEmpty() && !md.getParameters().isEmpty())
                    continue;

                VMethod jm = new JavaMethod(md, this);

                for (org.tlsys.lex.members.ClassModificator m : modificators) {
                    jm = m.onAddMethod(jm);
                }

                methods.add(jm);
                typeDeclaration.getMembers().remove(md);
                return Optional.of(jm);
            }
        }

        for (VMethod m : methods) {
            if (!m.getName().equals(name))
                continue;
        }
        return null;
    }

    @Override
    public TClassLoader getClassLoader() {
        return classLoader;
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

    @Override
    public VMember getParent() {
        return parent;
    }
}
