package org.tlsys;

import org.tlsys.lex.members.TArgument;
import org.tlsys.lex.members.VClass;

@FunctionalInterface
public interface ArgumentRequid {
    public boolean test(TArgument argument, VClass clazz);
}
