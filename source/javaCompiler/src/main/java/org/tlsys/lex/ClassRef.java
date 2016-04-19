package org.tlsys.lex;

import org.tlsys.lex.TExpression;
import org.tlsys.lex.members.VClass;

public interface ClassRef extends TExpression {
    public VClass getRef();
}
