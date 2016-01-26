package org.tlsys.lex.declare;

import java.lang.reflect.Modifier;

public class ArrayClass extends VClass {

    private static final long serialVersionUID = 8031564067794850457L;
    private VField lengthField;
    private VMethod get;
    private VMethod set;

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
        try {
            extendsClass = intType.getClassLoader().loadClass(Object.class.getName());
        } catch (VClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        lengthField = new VField(intType, Modifier.PUBLIC | Modifier.FINAL, null, this);
        lengthField.name = "length";
        fields.add(lengthField);

        get = new VMethod(this, null, null);
        get.name = "get";
        get.arguments.add(new VArgument(intType, "index", false));

        set = new VMethod(this, null, null);
        set.name = "set";
        set.arguments.add(new VArgument(intType, "index", false));
        set.arguments.add(new VArgument(component, "value", false));

        methods.add(get);
        methods.add(set);
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
