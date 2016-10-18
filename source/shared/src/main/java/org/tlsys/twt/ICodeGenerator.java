package org.tlsys.twt;

import org.tlsys.Outbuffer;
import org.tlsys.lex.Operation;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.Member;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VExecute;

import java.io.PrintStream;

public interface ICodeGenerator {
    void generateClass(GenerationContext context, CompileModuls.ClassRecord record, Outbuffer ps) throws CompileException;
    boolean operation(GenerationContext context, Operation operation, Outbuffer out) throws CompileException;
    default void generateExecute(GenerationContext context, VExecute execute, Outbuffer ps, CompileModuls moduls) throws CompileException {
        throw new RuntimeException("Not supported");
    }
}
