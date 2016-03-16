package org.tlsys.twt.desc;

import java.io.Serializable;

public class ClassDesc implements Serializable {
    private String name;
    private String alias;
    private MethodDesc[] methods;
    private ConstructorDesc[] constructors;
    private FieldDesc[] fields;
    private boolean staticFlag;
    private String[] staticBlock;
    private boolean compile;
    private String extend;
    private String[] implement;

    public ClassDesc(String name, String alias, MethodDesc[] methods, ConstructorDesc[] constructors, FieldDesc[] fields, boolean staticFlag, String[] staticBlock, boolean compile, String extend, String[] implement) {
        this.name = name;
        this.alias = alias;
        this.methods = methods;
        this.constructors = constructors;
        this.fields = fields;
        this.staticFlag = staticFlag;
        this.staticBlock = staticBlock;
        this.compile = compile;
        this.extend = extend;
        this.implement = implement;
    }

    public ClassDesc() {
    }

    public boolean isCompile() {
        return compile;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public MethodDesc[] getMethods() {
        return methods;
    }

    public FieldDesc[] getFields() {
        return fields;
    }

    public boolean isStaticFlag() {
        return staticFlag;
    }

    public String[] getStaticBlock() {
        return staticBlock;
    }

    public String getExtend() {
        return extend;
    }

    public String[] getImplement() {
        return implement;
    }

    public ConstructorDesc[] getConstructors() {
        return constructors;
    }
}
