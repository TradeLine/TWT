package org.tlsys.twt.desc;

public class MemberDesc {
    private String jsName;
    private boolean staticFlag;

    public MemberDesc(String jsName, boolean staticFlag) {
        this.jsName = jsName;
        this.staticFlag = staticFlag;
    }

    public MemberDesc() {
    }

    public String getJsName() {
        return jsName;
    }

    public boolean isStatic() {
        return staticFlag;
    }
}
