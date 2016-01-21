package org.tlsys.twt.desc.builder;

import org.tlsys.twt.desc.ArgumentDesc;
import org.tlsys.twt.desc.ExeDesc;

import java.util.ArrayList;

public abstract class ExeBuilder<RESULT extends ExeDesc, PARENT, SELF extends ExeBuilder> extends MemberBuilder<RESULT, PARENT, SELF>  {
    protected ArrayList<ArgumentDesc> arguments = new ArrayList<>();
    protected String body;

    public SELF body(String body) {
        this.body = body;
        return (SELF) this;
    }

    public ArgumentBuilder<SELF> argument() {
        return new ArgumentBuilder<SELF>((eeee)->{
            arguments.add(eeee);
            return (SELF)ExeBuilder.this;
        });
    }


}
