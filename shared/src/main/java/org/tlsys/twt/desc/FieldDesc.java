package org.tlsys.twt.desc;

public class FieldDesc extends MemberDesc {
    private String init;
    private String name;
    private TypeDesc type;

    public FieldDesc(String name, String jsName, boolean staticFlag, String init, TypeDesc type) {
        super(jsName, staticFlag);
        this.name = name;
        this.init = init;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public TypeDesc getType() {
        return type;
    }

    public FieldDesc() {
    }

    public String getInit() {
        return init;
    }
}
