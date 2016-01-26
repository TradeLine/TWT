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

    public FieldRecord(String jsName, String name, ClassRecord parent) {
        this.jsName = jsName;
        this.name = name;
        this.parent = parent;
    }
}
