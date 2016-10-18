package org.tlsys.twt;

import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VExecute;

public interface GenerationContext {
    VClass getCurrentClass();
    ICodeGenerator getGenerator(VClass clazz);
    ICodeGenerator getGenerator(VExecute execute);
    InvokeGenerator getInvokeGenerator(VExecute execute);
    CompileModuls getCompileModuls();
    String genLocalName();
}
