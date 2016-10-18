package org.tlsys.lex.declare;

import org.tlsys.lex.CanUse;
import org.tlsys.lex.Using;

import java.io.Serializable;
import java.lang.reflect.Modifier;

public interface Member extends CanUse, Using,Serializable {
    int getModificators();
    boolean isThis(String name);
    VClass getParent();

    default boolean isStatic() {
        return Modifier.isStatic(getModificators());
    }

    default boolean isInterface() {
        return Modifier.isInterface(getModificators());
    }
}
