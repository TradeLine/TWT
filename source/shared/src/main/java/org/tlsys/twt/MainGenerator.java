package org.tlsys.twt;

import org.tlsys.lex.Value;
import org.tlsys.lex.declare.VClassLoader;
import org.tlsys.lex.declare.VMethod;

import java.io.PrintStream;

public interface MainGenerator {
    public void generate(VClassLoader projectClassLoader, CompileModuls compileModuls, PrintStream ps) throws CompileException;
    public void generateInvoke(VMethod method, PrintStream out, Value ... arguments) throws CompileException;
}
