package org.tlsys.lex.members;

import org.tlsys.ArgumentRequid;

import java.util.Optional;

public interface VClass extends VMember {
    public Optional<VClass> getClass(String name);

    public Optional<VMethod> findMethod(String name, ArgumentRequid... requids);
}
