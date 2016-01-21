package org.tlsys.twt;

import org.tlsys.lex.Operation;
import org.tlsys.lex.declare.Member;

import java.io.PrintStream;

public interface ICodeGenerator {
    public boolean member(GenerationContext ctx, Member member, PrintStream ps) throws CompileException;

    public boolean operation(GenerationContext context, Operation operation, PrintStream out) throws CompileException;
}
