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
        lengthField.name = "_";
        lengthField.alias = "length";
        lengthField.init = new Const(0, intType);
        fields.add(lengthField);

        jsArray = new VField(intType, Modifier.PRIVATE | Modifier.FINAL, null, this);
        jsArray.name = "_f";
        jsArray.alias = "jsArray";
        jsArray.init = new Const(null, extendsClass);
        fields.add(jsArray);



        get = new VMethod(this, null, null);
        get.name = "_g";
        get.alias="get";
        get.arguments.add(new VArgument(intType, "i", false));
        get.block = new VBlock(get);
        get.returnType = component;

        set = new VMethod(this, null, null);
        set.name = "_s";
        set.alias="set";
        set.arguments.add(new VArgument(intType, "i", false));
        set.arguments.add(new VArgument(component, "v", false));
        set.returnType = intType.getClassLoader().loadClass("void");
        set.block = new VBlock(set);

        set.generator = ArrayCodeGenerator.class.getName();
        get.generator = ArrayCodeGenerator.class.getName();

        methods.add(get);
        methods.add(set);

        constructor = new VConstructor(this, null);
        constructor.name="_$";
        constructor.arguments.add(new VArgument(intType, "l", false));
        constructor.generator = ArrayCodeGenerator.class.getName();
        constructor.block = new VBlock(constructor);
        constructor.returnType = intType.getClassLoader().loadClass("void");
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
