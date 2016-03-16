package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.SVar;

public class VArgument extends SVar {
    private static final long serialVersionUID = 8365717984255691676L;
    public final boolean var;
    public final  boolean generic;

    public VArgument(String realName, VClass clazz, boolean var, boolean generic) {
        super(realName, clazz);
        this.var = var;
        this.generic = generic;
    }

    public VArgument(String realName, String alias, VClass clazz, boolean var, boolean generic) {
        super(realName, alias, clazz);
        this.var = var;
        this.generic = generic;
    }
}
