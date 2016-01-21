package org.tlsys.twt.desc.builder;

import org.tlsys.twt.desc.*;

import java.util.ArrayList;

public class ClassDescBuilder {
    private String name;
    private String alias;
    private ArrayList<MethodDesc> methods = new ArrayList<>();
    private ArrayList<ConstructorDesc> constructors = new ArrayList<>();
    private ArrayList<FieldDesc> fields = new ArrayList<>();
    private boolean staticFlag;
    private ArrayList<String> staticBlock = new ArrayList<>();
    private boolean compile;
    private String extend;
    private ArrayList<String> implement = new ArrayList<>();

    public FieldBuilder<ClassDescBuilder> field() {
        return new FieldBuilder<ClassDescBuilder>(f->{
            fields.add(f);
            return this;
        });
    }

    public MethodBuilder<ClassDescBuilder> method() {
        return new MethodBuilder<ClassDescBuilder>(m->{
            methods.add(m);
            return this;
        });
    }

    public ConstructorBuilder<ClassDescBuilder> constructor() {
        return new ConstructorBuilder<ClassDescBuilder>(c->{
            constructors.add(c);
            return this;
        });
    }

    public ClassDescBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ClassDescBuilder alias(String alias) {
        this.alias = alias;
        return this;
    }

    public ClassDescBuilder staticFlag(boolean staticFlag) {
        this.staticFlag = staticFlag;
        return this;
    }

    public ClassDescBuilder compile(boolean compile) {
        this.compile = compile;
        return this;
    }

    public ClassDescBuilder staticBlock(String text) {
        staticBlock.add(text);
        return this;
    }

    public ClassDesc build() {
        ClassDesc cd = new ClassDesc(name, alias, methods.stream().toArray(MethodDesc[]::new), constructors.stream().toArray(ConstructorDesc[]::new), fields.stream().toArray(FieldDesc[]::new), staticFlag, staticBlock.stream().toArray(String[]::new), compile, extend, implement.stream().toArray(String[]::new));
        return cd;
    }

    public ClassDescBuilder extend(String extend) {
        this.extend = extend;
        return this;
    }

    public ClassDescBuilder implement(String implement) {
        this.implement.add(implement);
        return this;
    }
}
