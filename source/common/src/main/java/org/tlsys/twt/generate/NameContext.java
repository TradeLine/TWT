package org.tlsys.twt.generate;

import org.tlsys.twt.members.TConstructor;
import org.tlsys.twt.members.TField;
import org.tlsys.twt.members.VClass;
import org.tlsys.twt.members.VMethod;

public interface NameContext {
    public String getName(VClass clazz);
    public String getName(TConstructor constructor);
    public String getName(VMethod method);
    public String getName(TField field);
}
