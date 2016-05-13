package org.tlsys.twt;

import org.tlsys.twt.links.ClassVal;
import org.tlsys.twt.members.TArgument;

@FunctionalInterface
public interface ArgumentValidator {
    public boolean isValid(TArgument argument, ClassVal type);
}
