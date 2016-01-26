package org.tlsys.twt.classes;

import org.tlsys.twt.JArray;
import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
public class ClassRecord {
    private String jsName;
    private String name;
    private JArray<FieldRecord> fields = new JArray<>();

    public ClassRecord(String jsName, String name) {
        this.jsName = jsName;
        this.name = name;
    }

    public ClassRecord addField(String jsName, String name) {
        FieldRecord fr = new FieldRecord(jsName, name, this);
        fields.add(fr);
        return this;
    }

    public String getJsName() {
        return jsName;
    }

    public Class convertToClass() {
        return null;
    }
}
