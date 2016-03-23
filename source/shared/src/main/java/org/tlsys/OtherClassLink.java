package org.tlsys;

import org.tlsys.lex.ClassModificator;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VField;
import org.tlsys.lex.declare.VMethod;

import java.util.List;
import java.util.Optional;

public class OtherClassLink implements ClassModificator {

    private final VClass toClass;
    private final VField field;

    public VField getField() {
        return field;
    }

    public OtherClassLink(VClass forClass, VClass toClass) {
        this.toClass = toClass;
        field = new VField("c"+Integer.toString(toClass.hashCode()).replace('-','_'), null, toClass, 0, forClass);
    }

    @Override
    public List<VField> getFields(List<VField> fields) {
        fields.add(field);
        return fields;
    }

    @Override
    public List<VMethod> getMethods(List<VMethod> fields) {
        return fields;
    }

    public static OtherClassLink getOrCreate(VClass forClass, VClass toClass) {
        Optional<ClassModificator> cm = forClass.getModificator(e->{
            if (e instanceof OtherClassLink) {
                OtherClassLink m = (OtherClassLink)e;
                if (m.toClass == toClass)
                    return true;
            }
            return false;
        });

        if (cm.isPresent())
            return (OtherClassLink) cm.get();

        OtherClassLink ocl = new OtherClassLink(forClass, toClass);
        forClass.addMod(ocl);
        return ocl;
    }
}
