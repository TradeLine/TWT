package org.tlsys.twt.desc;

public class ArgumentDesc {
    private TypeDesc type;
    private String name;
    private String jsName;

    public ArgumentDesc(String name, String jsName, TypeDesc type) {
        this.name = name;
        this.jsName = jsName;
        this.type = type;
    }

    public ArgumentDesc() {
    }

    public TypeDesc getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getJsName() {
        return jsName;
    }
}
