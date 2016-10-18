package org.tlsys;

import org.tlsys.lex.declare.VBlock;
import org.tlsys.lex.declare.VExecute;

public interface MethodBodyBuilder {
    VBlock buildMethodBody(VExecute execute);
}
