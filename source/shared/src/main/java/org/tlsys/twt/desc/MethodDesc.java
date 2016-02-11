package org.tlsys.twt.desc;

public class MethodDesc extends ExeDesc {
    private String name;
    private TypeDesc result;

    public MethodDesc(String name, String jsName, boolean staticFlag, TypeDesc result, ArgumentDesc[] arguments, String body) {
        super(jsName, staticFlag, arguments, body);
        this.name = name;
        this.result = result;
    }

    public MethodDesc() {
    }

    public String getName() {
        return name;
    }

    public TypeDesc getResult() {
        return result;
    }
}
