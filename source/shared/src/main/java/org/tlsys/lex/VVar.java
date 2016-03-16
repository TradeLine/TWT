package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;

import java.util.Optional;
import java.util.function.Predicate;

public class VVar extends SVar {


    private static final long serialVersionUID = -337151531062470578L;

    public VVar(String realName, VClass clazz) {
        super(realName, clazz);
    }

    public VVar(String realName, String alias, VClass clazz) {
        super(realName, alias, clazz);
    }
}
