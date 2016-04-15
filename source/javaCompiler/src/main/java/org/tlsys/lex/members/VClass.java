package org.tlsys.lex.members;

import java.util.Optional;

public interface VClass extends VMember {
    public String getName();
    public Optional<VClass> getClass(String name);

    public Optional<VMethod> findMethod(String name, MehtodSearchRequest request);

    public TClassLoader getClassLoader();
}
