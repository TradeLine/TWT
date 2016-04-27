package org.tlsys;

import org.tlsys.twt.generate.NameContext;
import org.tlsys.twt.members.TConstructor;
import org.tlsys.twt.members.TField;
import org.tlsys.twt.members.VClass;
import org.tlsys.twt.members.VMethod;

import java.util.HashMap;

public class SimpleNameContext implements NameContext {
    private class ClassRecord {
        final String classRuntimeName;
        long constructorIterator;
        final HashMap<TConstructor, String> constructors = new HashMap<>();

        public ClassRecord(String classRuntimeName) {
            this.classRuntimeName = classRuntimeName;
        }
    }
    HashMap<VClass, ClassRecord> classes = new HashMap<>();
    HashMap<VMethod, String> methods = new HashMap<>();
    HashMap<TField, String> fields = new HashMap<>();

    private long classNameIterator;
    private long methodNameIterator;
    private long fieldNameIterator;

    @Override
    public String getName(VClass clazz) {
        ClassRecord cr = classes.get(clazz);
        if (cr != null) {
            return cr.classRuntimeName;
        }
        cr = new ClassRecord("c"+Long.toString(classNameIterator++, Character.MAX_RADIX).replace('-', '_'));
        classes.put(clazz, cr);
        return cr.classRuntimeName;
    }

    @Override
    public String getName(TConstructor clazz) {
        ClassRecord cr = classes.get(clazz.getParent());
        if (cr == null) {
            cr = new ClassRecord("c"+Long.toString(classNameIterator++, Character.MAX_RADIX).replace('-', '_'));
            classes.put(clazz.getParent(), cr);
        }

        String out = cr.constructors.get(clazz);
        if (out != null)
            return out;
        out = "d"+Long.toString(cr.constructorIterator++, Character.MAX_RADIX).replace('-', '_');
        cr.constructors.put(clazz, out);
        return out;
    }

    @Override
    public String getName(VMethod clazz) {
        String out = methods.get(clazz);
        if (out != null)
            return out;

        out = "m"+Long.toString(methodNameIterator++, Character.MAX_RADIX).replace('-', '_');
        methods.put(clazz, out);
        return out;
    }

    @Override
    public String getName(TField field) {
        String out = fields.get(field);
        if (out != null)
            return out;

        out = "f"+Long.toString(fieldNameIterator++, Character.MAX_RADIX).replace('-', '_');
        fields.put(field, out);
        return out;
    }
}
