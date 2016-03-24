package org.tlsys.lex;

import org.tlsys.lex.declare.VClass;

public class VVar extends SVar {


    private static final long serialVersionUID = -337151531062470578L;

    public VVar(String realName, VClass clazz, Context context) {
        super(realName, clazz, context);
    }

    public VVar(String realName, String alias, VClass clazz, Context context) {
        super(realName, alias, clazz, context);
    }
}
