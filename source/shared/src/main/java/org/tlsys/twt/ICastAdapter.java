package org.tlsys.twt;

import org.tlsys.lex.Value;
import org.tlsys.lex.declare.VClass;
import org.tlsys.sourcemap.SourcePoint;

public interface ICastAdapter {
    public Value cast(Value value, VClass to, SourcePoint point) throws CompileException;
}
