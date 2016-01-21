package org.tlsys.lex.declare;

import java.lang.reflect.Modifier;

public class ArrayClass extends VClass {

    private static final long serialVersionUID = 8031564067794850457L;
    private VField lengthField;

    public ArrayClass(VClass component, VClass intType) {
        this(component);
        init(intType);
    }

    public ArrayClass(VClass component) {
        super(null, null);
        this.component = component;
        setClassLoader(component.getClassLoader());
    }

    public void init(VClass intType) {
        if (lengthField != null)
            throw new IllegalStateException("Array type already inited");
        lengthField = new VField(intType, Modifier.PUBLIC | Modifier.FINAL, null, this);
        lengthField.name = "length";
        fields.add(lengthField);
    }

    private VClass component;

    public VClass getComponent() {
        return component;
    }

    @Override
    public String toString() {
        return "ArrayClass{" +
                "component=" + component +
                '}';
    }
}
