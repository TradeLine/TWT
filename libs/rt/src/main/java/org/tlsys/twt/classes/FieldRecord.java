package org.tlsys.twt.classes;

import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
public class FieldRecord {
    private String jsName;
    private String name;
    private ClassRecord parent;
    private String initValue;
    private TypeProvider type;
    private int mods;

    public FieldRecord(String jsName, String name, ClassRecord parent, TypeProvider type, String initValue, int mods) {
        this.jsName = jsName;
        this.name = name;
        this.parent = parent;
        this.type = type;
        this.initValue = initValue;
        this.mods = mods;
    }

    public int getModificators() {
        return mods;
    }

    public String getJsName() {
        return jsName;
    }

    public String getName() {
        return name;
    }

    public ClassRecord getParent() {
        return parent;
    }

    public String getInitValue() {
        return initValue;
    }

    public TypeProvider getType() {
        return type;
    }
}
