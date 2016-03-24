package org.tlsys.lex.declare;

import org.tlsys.ArgumentModificator;
import org.tlsys.lex.Context;
import org.tlsys.lex.SVar;

public class VArgument extends SVar {
    private static final long serialVersionUID = 8365717984255691676L;
    public final boolean var;
    public final  boolean generic;

    private final ArgumentModificator creator;

    public ArgumentModificator getCreator() {
        return creator;
    }

    public VArgument(String realName, VClass clazz, boolean var, boolean generic, Context method, ArgumentModificator creator) {
        super(realName, clazz, method);
        this.var = var;
        this.generic = generic;
        this.creator = creator;
    }

    public VArgument(String realName, String alias, VClass clazz, boolean var, boolean generic, Context method, ArgumentModificator creator) {
        super(realName, alias, clazz, method);
        this.var = var;
        this.generic = generic;
        this.creator = creator;
    }
}
