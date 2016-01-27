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
    private ValueProvider initValue;
    private TypeProvider type;

    public FieldRecord(String jsName, String name, ClassRecord parent, TypeProvider type, ValueProvider initValue) {
        this.jsName = jsName;
        this.name = name;
        this.parent = parent;
        this.type = type;
        this.initValue = initValue;
    }
}
