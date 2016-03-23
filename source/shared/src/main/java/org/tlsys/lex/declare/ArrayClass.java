package org.tlsys.lex.declare;

import org.tlsys.lex.Collect;
import org.tlsys.lex.Const;
import org.tlsys.twt.CompileException;

import java.lang.reflect.Modifier;
import java.util.Objects;

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
        super(component.getSimpleRealName()+"[]");
        this.component = Objects.requireNonNull(component, "Component is NULL");
        setClassLoader(component.getClassLoader());

        name = "["+component.name;
        alias = "["+component.alias;
        fullName = "[" + component.fullName;
    }

    @Override
    public String getRealName() {
        return "[L"+component.getRealName()+";";
    }

    public void init(VClass intType) throws CompileException {
        if (lengthField != null)
            throw new IllegalStateException("Array type already inited");

        parentContext = component.getParentContext();
        //VClass classClass;
        extendsClass = intType.getClassLoader().loadClass(Object.class.getName());
        lengthField = new VField(LENGTH, "length", intType, Modifier.PUBLIC, this);
        lengthField.init = new Const(0, intType);
        fields.add(lengthField);

        jsArray = new VField(ARRAY, "jsArray", intType, Modifier.PRIVATE, this);
        jsArray.init = new Const(null, extendsClass);
        fields.add(jsArray);



        get = new VMethod(this, null);
        get.setRuntimeName(GET);
        get.alias="get";
        get.arguments.add(new VArgument("i", intType, false, false, null));
        get.block = new VBlock(get);
        get.returnType = component;
        get.setModificators(Modifier.PUBLIC);

        set = new VMethod(this, null);
        set.setRuntimeName(SET);
        set.alias="set";
        set.arguments.add(new VArgument("i", intType, false, false, null));
        set.arguments.add(new VArgument("v", component, false, false, null));
        set.returnType = intType.getClassLoader().loadClass("void");
        set.block = new VBlock(set);
        set.setModificators(Modifier.PUBLIC);

        set.generator = ArrayCodeGenerator.class.getName();
        get.generator = ArrayCodeGenerator.class.getName();

        methods.add(get);
        methods.add(set);

        constructor = new VConstructor(this);
        constructor.setModificators(Modifier.PUBLIC);
        constructor.setRuntimeName(CONSTRUCTOR);
        constructor.arguments.add(new VArgument("l", intType, false, false, null));
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
