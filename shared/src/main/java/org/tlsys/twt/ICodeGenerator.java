package org.tlsys.twt;

import org.tlsys.lex.Operation;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.Member;
import org.tlsys.lex.declare.VClass;

import java.io.PrintStream;

public interface ICodeGenerator {
    public void generateClass(GenerationContext context, CompileModuls.ClassRecord record, PrintStream ps) throws CompileException;
    public boolean operation(GenerationContext context, Operation operation, PrintStream out) throws CompileException;
}
