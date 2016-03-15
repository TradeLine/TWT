package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;

public class AnnonimusClass extends VClass {
    public AnnonimusClass(VClass parent, Symbol.ClassSymbol classSymbol) {
        super(null, parent, parent, classSymbol);
    }
}
