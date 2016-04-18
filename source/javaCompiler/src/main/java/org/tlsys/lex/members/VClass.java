package org.tlsys.lex.members;

import org.tlsys.lex.Named;

import java.util.Optional;

public interface VClass extends VMember, Named {
    public Optional<VClass> getClass(String name);

    public Optional<VMethod> findMethod(String name, MehtodSearchRequest request);

    public TClassLoader getClassLoader();
}
