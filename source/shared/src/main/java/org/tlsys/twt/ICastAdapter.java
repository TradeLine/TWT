package org.tlsys.twt;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.VClass;

public interface ICastAdapter {
    public Value cast(Value value, VClass to) throws CompileException;
}
