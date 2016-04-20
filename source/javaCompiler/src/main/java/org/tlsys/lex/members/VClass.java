package org.tlsys.lex.members;

import org.tlsys.lex.Named;

import java.util.Optional;

public interface VClass extends VMember, Named {
    public String getSimpleName();
    public Optional<VClass> getClass(String name);

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


}
