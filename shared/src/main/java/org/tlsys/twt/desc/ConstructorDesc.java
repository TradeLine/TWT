package org.tlsys.twt.desc;

public class ConstructorDesc extends ExeDesc {
    private ArgumentDesc[] superArgs;

    public ConstructorDesc(String jsName, boolean staticFlag, ArgumentDesc[] arguments, ArgumentDesc[] superArgs, String body) {
        super(jsName, staticFlag, arguments, body);
        this.superArgs = superArgs;
    }

    public ConstructorDesc() {
    }

    public ArgumentDesc[] getSuperArgs() {
        return superArgs;
    }
}
