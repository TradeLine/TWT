package org.tlsys.lex.declare;

import org.tlsys.lex.Const;
import org.tlsys.lex.Invoke;
import org.tlsys.lex.This;
import org.tlsys.twt.CompileException;

import java.lang.reflect.Modifier;

public class ArrayClass extends VClass {

    private static final long serialVersionUID = 8031564067794850457L;
    public VField lengthField;
    public VField jsArray;
    public VMethod get;
    public VMethod set;
    public VConstructor constructor;
    private VClass component;

    public ArrayClass(VClass component, VClass intType) {
        this(component);
        try {
            init(intType);
        } catch (CompileException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayClass(VClass component) {
        super(null, null);
        this.component = component;
        setClassLoader(component.getClassLoader());
    }

    public void init(VClass intType) throws CompileException {
        if (lengthField != null)
            throw new IllegalStateException("Array type already inited");
        //VClass classClass;
        extendsClass = intType.getClassLoader().loadClass(Object.class.getName());
        lengthField = new VField(intType, Modifier.PUBLIC | Modifier.FINAL, null, this);
        lengthField.name = "length";
        lengthField.init = new Const(0, intType);
        fields.add(lengthField);

        jsArray = new VField(intType, Modifier.PRIVATE | Modifier.FINAL, null, this);
        jsArray.name = "jsArray";
        jsArray.init = new Const(null, extendsClass);
        fields.add(jsArray);



        get = new VMethod(this, null, null);
        get.name = "get";
        get.arguments.add(new VArgument(intType, "index", false));

        set = new VMethod(this, null, null);
        set.name = "set";
        set.arguments.add(new VArgument(intType, "index", false));
        set.arguments.add(new VArgument(component, "value", false));

        set.generator = ArrayCodeGenerator.class.getName();
        get.generator = ArrayCodeGenerator.class.getName();

        methods.add(get);
        methods.add(set);

        constructor = new VConstructor(this, null);
        constructor.arguments.add(new VArgument(intType, "length", false));
        constructor.generator = ArrayCodeGenerator.class.getName();
        constructors.add(constructor);
    }

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
