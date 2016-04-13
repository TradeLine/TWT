package org.tlsys.lex.declare;

import org.tlsys.ArgumentModificator;
import org.tlsys.HavinSourceStart;
import org.tlsys.lex.Context;
import org.tlsys.lex.SVar;
import org.tlsys.sourcemap.SourcePoint;

public class VArgument extends SVar implements HavinSourceStart {
    private static final long serialVersionUID = 8365717984255691676L;
    public final boolean var;
    public final  boolean generic;
    private final ArgumentModificator creator;
    private SourcePoint point;

    public VArgument(String realName, String alias, VClass clazz, boolean var, boolean generic, Context method, ArgumentModificator creator, SourcePoint point) {
        super(realName, alias, clazz, method);
        this.var = var;
        this.generic = generic;
        this.creator = creator;
        this.point = point;
    }

    public VArgument(String realName, String alias, VClass clazz, boolean var, boolean generic, Context method, ArgumentModificator creator) {
        this(realName, alias, clazz, var, generic, method, creator, null);
    }

    public ArgumentModificator getCreator() {
        return creator;
    }

    @Override
    public SourcePoint getStartPoint() {
        return point;
    }
}
