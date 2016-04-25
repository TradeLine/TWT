package org.tlsys;

import org.tlsys.twt.members.TArgument;
import org.tlsys.twt.members.VClass;

@FunctionalInterface
public interface ArgumentRequid {
    public boolean test(TArgument argument, VClass clazz);
}
