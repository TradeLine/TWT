package org.tlsys.twt;

import org.tlsys.lex.declare.VClassLoader;

import java.io.PrintStream;

public interface MainGenerator {
    public void generate(VClassLoader projectClassLoader, CompileModuls compileModuls, PrintStream ps) throws CompileException;
}
