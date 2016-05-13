package org.tlsys.java.lex;

import com.github.javaparser.ast.body.VariableDeclarator;
import org.tlsys.JavaCompiller;
import org.tlsys.twt.expressions.AnntationItem;
import org.tlsys.twt.expressions.TExpression;
import org.tlsys.twt.links.ClassVal;
import org.tlsys.twt.members.TField;
import org.tlsys.twt.members.VClass;
import org.tlsys.twt.members.VMember;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class JavaField extends JavaMember implements TField {
    private static final long serialVersionUID = -1956507779908166638L;

    private final VariableDeclarator vd;
    private final String name;
    private final ClassVal type;
    private final int modifiers;
    private TExpression init;

    public JavaField(VariableDeclarator vd, int modifiers, ClassVal type, VClass parent) {
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
    public ClassVal getType() {
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

    @Override
    public List<AnntationItem> getList() {
        return Collections.EMPTY_LIST;
    }
}
