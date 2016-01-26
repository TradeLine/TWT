package org.tlsys.twt.classes;

import org.tlsys.twt.JArray;
import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
public class MethodRecord {
    private ClassRecord parent;
    private final JArray<ArgumentRecord> arguments = new JArray<>();
    private String jsName;
    private String name;
    private String body;

    public MethodRecord(String jsName, String name, String body) {
        this.body = body;
        this.jsName = jsName;
        this.name = name;
    }

    public MethodRecord addArg(ArgumentRecord ar) {
        arguments.add(ar);
        return this;
    }
}
