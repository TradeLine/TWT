package org.tlsys.lex.declare;

import org.tlsys.lex.Collect;
import org.tlsys.lex.Const;
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

    public static final String CONSTRUCTOR = "_$";
    public static final String SET = "_s";
    public static final String GET = "_g";
    public static final String ARRAY = "_f";
    public static final String LENGTH = "_";

    public ArrayClass(VClass component, VClass intType) {
        this(component);
        try {
            init(intType);
        } catch (CompileException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getUsing(Collect c) {
        super.getUsing(c);
        c.add(getComponent());
    }

    public ArrayClass(VClass component) {
        super(null, null);
        this.component = component;
        setClassLoader(component.getClassLoader());

        name = "["+component.name;
        alias = "["+component.alias;
        fullName = "[" + component.fullName;
    }

    public void init(VClass intType) throws CompileException {
        if (lengthField != null)
            throw new IllegalStateException("Array type already inited");
        //VClass classClass;
        extendsClass = intType.getClassLoader().loadClass(Object.class.getName());
        lengthField = new VField(intType, Modifier.PUBLIC, null, this);
        lengthField.name = LENGTH;
        lengthField.alias = "length";
        lengthField.init = new Const(0, intType);
        fields.add(lengthField);

        jsArray = new VField(intType, Modifier.PRIVATE, null, this);
        jsArray.name = ARRAY;
        jsArray.alias = "jsArray";
        jsArray.init = new Const(null, extendsClass);
        fields.add(jsArray);



        get = new VMethod(this, null, null);
        get.setRuntimeName(GET);
        get.alias="get";
        get.arguments.add(new VArgument(intType, "i", false));
        get.block = new VBlock(get);
        get.returnType = component;
        get.setModificators(Modifier.PUBLIC);

        set = new VMethod(this, null, null);
        set.setRuntimeName(SET);
        set.alias="set";
        set.arguments.add(new VArgument(intType, "i", false));
        set.arguments.add(new VArgument(component, "v", false));
        set.returnType = intType.getClassLoader().loadClass("void");
        set.block = new VBlock(set);
        set.setModificators(Modifier.PUBLIC);

        set.generator = ArrayCodeGenerator.class.getName();
        get.generator = ArrayCodeGenerator.class.getName();

        methods.add(get);
        methods.add(set);

        constructor = new VConstructor(this, null);
        constructor.setModificators(Modifier.PUBLIC);
        constructor.setRuntimeName(CONSTRUCTOR);
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
