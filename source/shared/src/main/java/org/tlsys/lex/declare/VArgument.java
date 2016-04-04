package org.tlsys.lex.declare;

import org.tlsys.ArgumentModificator;
import org.tlsys.lex.Context;
import org.tlsys.lex.SVar;
import org.tlsys.sourcemap.SourcePoint;

public class VArgument extends SVar {
    private static final long serialVersionUID = 8365717984255691676L;
    public final boolean var;
    public final  boolean generic;
    private SourcePoint point;

    private final ArgumentModificator creator;

    public ArgumentModificator getCreator() {
        return creator;
    }

    public VArgument(String realName, VClass clazz, boolean var, boolean generic, Context method, ArgumentModificator creator, SourcePoint point) {
        super(realName, clazz, method);
        this.var = var;
        this.generic = generic;
        this.creator = creator;
        this.point = point;
    }

    public VArgument(String realName, String alias, VClass clazz, boolean var, boolean generic, Context method, ArgumentModificator creator) {
        super(realName, alias, clazz, method);
        this.var = var;
        this.generic = generic;
        this.creator = creator;
    }

    public SourcePoint getPoint() {
        return point;
    }
}
