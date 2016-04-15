package org.tlsys;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import org.tlsys.lex.members.TArgument;
import org.tlsys.lex.members.VClass;
import org.tlsys.lex.members.VMember;
import org.tlsys.lex.members.VMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class JavaMethod implements VMethod {

    private final transient MethodDeclaration methodDeclaration;

    private final String realName;
    private final VClass parent;
    private final int modifiers;
    private VClass result;
    private List<TArgument> arguments;

    public JavaMethod(MethodDeclaration methodDeclaration, VClass parent) {
        this.methodDeclaration = methodDeclaration;
        this.parent = parent;

        realName = methodDeclaration.getName();

        modifiers = methodDeclaration.getModifiers();
    }

    @Override
    public String getName() {
        return realName;
    }

    @Override
    public VClass getResult() {
        if (result != null)
            return result;
        result = JavaCompiller.findClass(methodDeclaration.getType(), this);
        return result;
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    @Override
    public boolean add(VMember member) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public boolean remove(VMember member) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public <T extends VMember> Optional<T> getChild(Predicate<VMember> predicate) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public VClass getParent() {
        return parent;
    }

    @Override
    public List<TArgument> getArguments() {
        if (arguments != null)
            return arguments;

        arguments = new ArrayList<>();

        for (Parameter p : methodDeclaration.getParameters()) {
            arguments.add(new JavaArgument(p, this));
        }
        return arguments;
    }
}
