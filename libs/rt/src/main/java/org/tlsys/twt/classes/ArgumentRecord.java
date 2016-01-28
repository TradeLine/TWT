package org.tlsys.twt.classes;

import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
public class ArgumentRecord {
    private String name;
    private boolean var;
    private TypeProvider type;

    public ArgumentRecord(String name, boolean var, TypeProvider type) {
        this.name = name;
        this.var = var;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public boolean isVar() {
        return var;
    }

    public TypeProvider getType() {
        return type;
    }
}
