package org.tlsys;

import org.tlsys.lex.ClassModificator;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VConstructor;
import org.tlsys.lex.declare.VField;
import org.tlsys.lex.declare.VMethod;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;

/**
 * Модификатор, добовляющий поле, в котором хранится родитель
 */
public class ParentClassModificator implements ClassModificator {

    private final VField field;

    public ParentClassModificator(VClass forClass) {

        Optional<VClass> op = forClass.getDependencyParent();
        if (!op.isPresent())
            throw new IllegalArgumentException("Class " + forClass.getRealName() + " not need ParentContent");

        if (forClass.getModificator(e->e.getClass() == ParentClassModificator.class).isPresent())
            throw new IllegalArgumentException("Class " + forClass.getRealName() + " already have parent link");

        field = new VField("this$0", "this$0", op.get(), Modifier.PRIVATE | Modifier.FINAL, forClass);

        for (VConstructor c : forClass.constructors) {
            c.getMods().add(new ParentArgumentModif(c));
        }
    }

    public VField getParentField() {
        return field;
    }

    @Override
    public List<VField> getFields(List<VField> fields) {
        fields.add(field);
        return fields;
    }

    @Override
    public List<VMethod> getMethods(List<VMethod> methods) {
        return methods;
    }
}
