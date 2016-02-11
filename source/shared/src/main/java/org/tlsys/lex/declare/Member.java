package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.CanUse;
import org.tlsys.lex.HaveSymbol;
import org.tlsys.lex.Using;

import java.io.Serializable;
import java.lang.reflect.Modifier;

public interface Member extends HaveSymbol, CanUse, Using,Serializable {
    public int getModificators();
    public boolean isThis(String name);
    public VClass getParent();

    public default boolean isStatic() {
        return Modifier.isStatic(getModificators());
    }

    public default boolean isInterface() {
        return Modifier.isInterface(getModificators());
    }
}
