package org.tlsys.lex.members;

import java.util.Optional;

public interface VClass extends VMember {
    public Optional<VClass> getClass(String name);
}
