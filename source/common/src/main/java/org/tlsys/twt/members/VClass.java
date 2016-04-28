package org.tlsys.twt.members;

import java.util.List;
import java.util.Optional;

public interface VClass extends VMember, Named {
    public String getSimpleName();

    public String getSimpleRealTimeName();

    public String getRealTimeName();

    public VClass getSuperClass();

    @Override
    default String getName() {
        VMember m = getParent();
        if (m instanceof VClass)
            return ((VClass) m).getName() + "$" + getSimpleName();

        if (m instanceof VPackage) {
            if (((VPackage) m).getName() == null)
                return getSimpleName();
            return ((VPackage) m).getName() + "." + getSimpleName();
        }

        throw new RuntimeException("Unknown parent");
    }

    public Optional<VMethod> findMethod(String name, MehtodSearchRequest request);

    public TClassLoader getClassLoader();

    Optional<TField> getField(String name);

    public boolean isPrimitive();

    public List<VMember> getMembers();

    public Optional<TConstructor> findConstructor(MehtodSearchRequest request);
}
