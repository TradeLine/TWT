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
    private boolean staticFlag;

    public JArray<ArgumentRecord> getArguments() {
        return arguments;
    }

    public String getJsName() {
        return jsName;
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }

    public boolean isStaticFlag() {
        return staticFlag;
    }

    public MethodRecord(String jsName, String name, String body, boolean staticFlag) {
        this.body = body;
        this.jsName = jsName;
        this.name = name;
        this.staticFlag = staticFlag;
    }

    public MethodRecord addArg(ArgumentRecord ar) {
        arguments.add(ar);
        return this;
    }
}
