package org.tlsys.lex.declare;

import org.tlsys.lex.Collect;
import org.tlsys.lex.Const;
import org.tlsys.twt.CompileException;

import java.lang.reflect.Modifier;
import java.util.Objects;

public class ArrayClass extends VClass {

    public static final String CONSTRUCTOR = "_$";
    public static final String SET = "_s";
    public static final String GET = "_g";
    public static final String ARRAY = "_f";
    public static final String LENGTH = "_";
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
        super(component.getSimpleRealName()+"[]");
        this.component = Objects.requireNonNull(component, "Component is NULL");
        setClassLoader(component.getClassLoader());

        name = "["+component.name;
        alias = "["+component.alias;
        fullName = "[" + component.fullName;
    }

    @Override
    public void getUsing(Collect c) {
        super.getUsing(c);
        c.add(getComponent());
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



        get = new VMethod(null, "get", this, null);
        get.setRuntimeName(GET);
        get.alias="get";
        get.addArg(new VArgument("i", intType, false, false, get, null, null));
        get.setBlock(new VBlock(get, null, null));
        get.returnType = component;
        get.setModificators(Modifier.PUBLIC);

        set = new VMethod(null, "set", this, null);
        set.setRuntimeName(SET);
        set.alias="set";
        set.addArg(new VArgument("i", intType, false, false, set, null, null));
        set.addArg(new VArgument("v", component, false, false, set, null, null));
        set.returnType = intType.getClassLoader().loadClass("void");
        set.setBlock(new VBlock(set, null, null));
        set.setModificators(Modifier.PUBLIC);

        //set.generator = ArrayCodeGenerator.class.getName();
        //get.generator = ArrayCodeGenerator.class.getName();

        methods.add(get);
        methods.add(set);

        constructor = new VConstructor(null, this);
        constructor.setModificators(Modifier.PUBLIC);
        constructor.setRuntimeName(CONSTRUCTOR);
        constructor.addArg(new VArgument("l", intType, false, false, constructor, null, null));
        //constructor.generator = ArrayCodeGenerator.class.getName();
        constructor.setBlock(new VBlock(constructor, null, null));
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
