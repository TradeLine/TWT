package org.tlsys;

import org.tlsys.lex.declare.VClass;

public class NullClass extends VClass {

    private static final long serialVersionUID = 4915815860381948884L;


    protected NullClass() {
        super("NULL", null);
    }

    @Override
    public String getRealName() {
        return "null";
    }
}
