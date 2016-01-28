package org.tlsys.twt.classes;

import org.tlsys.twt.JArray;
import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.rt.java.lang.TClass;
import org.tlsys.twt.rt.java.lang.reflect.TField;

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

    public String getName() {
        return name;
    }

    public JArray<FieldRecord> getFields() {
        return fields;
    }

    public JArray<MethodRecord> getMethods() {
        return methods;
    }

    public ClassRecord addField(String jsName, String name, TypeProvider type, String initValue, boolean staticFlag) {
        FieldRecord fr = new FieldRecord(jsName, name, this, type, initValue, staticFlag);
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
}
