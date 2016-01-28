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
    private boolean staticFlag;

    public FieldRecord(String jsName, String name, ClassRecord parent, TypeProvider type, String initValue, boolean staticFlag) {
        this.jsName = jsName;
        this.name = name;
        this.parent = parent;
        this.type = type;
        this.initValue = initValue;
        this.staticFlag = staticFlag;
    }

    public boolean isStaticFlag() {
        return staticFlag;
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
