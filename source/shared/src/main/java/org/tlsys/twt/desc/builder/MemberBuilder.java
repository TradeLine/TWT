package org.tlsys.twt.desc.builder;

import org.tlsys.twt.desc.MemberDesc;

public abstract class MemberBuilder<RESULT extends MemberDesc, PARENT, SELF extends MemberBuilder> implements Builder<RESULT, PARENT> {
    protected String jsName;
    protected boolean staticFlag;


    public SELF jsName(String jsName) {
        this.jsName = jsName;
        return (SELF) this;
    }

    public SELF staticFlag(boolean staticFlag) {
        this.staticFlag = staticFlag;
        return (SELF)this;
    }
}
