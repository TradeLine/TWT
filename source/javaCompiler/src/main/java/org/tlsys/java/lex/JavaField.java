package org.tlsys.java.lex;

import com.github.javaparser.ast.body.VariableDeclarator;
import org.tlsys.JavaCompiller;
import org.tlsys.lex.TExpression;
import org.tlsys.lex.members.TField;
import org.tlsys.lex.members.VClass;
import org.tlsys.lex.members.VMember;

import java.util.Optional;
import java.util.function.Predicate;

public class JavaField extends JavaMember implements TField {
    private static final long serialVersionUID = -1956507779908166638L;

    private final VariableDeclarator vd;
    private final String name;
    private final VClass type;
    private final int modifiers;
    private TExpression init;

    public JavaField(VariableDeclarator vd, int modifiers, VClass type, VClass parent) {
        super(parent);
        this.vd = vd;
        this.name = vd.getId().getName();
        this.type = type;
        this.modifiers = modifiers;
    }

    @Override
    public TExpression getInitValue() {
        if (init != null)
            return init;
        if (vd.getInit() == null)
            init = JavaCompiller.getInitValueFor(getType());
        else
            init = JavaCompiller.expression(vd.getInit(), this, getType());
        return init;
    }

    @Override
    public VClass getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
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
        return (VClass) super.getParent();
    }
}
