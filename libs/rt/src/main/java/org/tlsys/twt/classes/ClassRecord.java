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
    private JArray<MethodRecord> methods = new JArray<>();

    public ClassRecord(String jsName, String name) {
        this.jsName = jsName;
        this.name = name;
    }

    public ClassRecord addField(String jsName, String name, TypeProvider type, ValueProvider initValue) {
        FieldRecord fr = new FieldRecord(jsName, name, this, type, initValue);
        fields.add(fr);
        return this;
    }

    public ClassRecord addMethod(MethodRecord mr) {
        methods.add(mr);
        return this;
    }

    public String getJsName() {
        return jsName;
    }

    public Class convertToClass() {
        return null;
    }
}
