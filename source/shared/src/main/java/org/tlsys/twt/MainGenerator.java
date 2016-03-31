package org.tlsys.twt;

import org.tlsys.Outbuffer;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.VClassLoader;
import org.tlsys.lex.declare.VMethod;

import java.io.PrintStream;

public interface MainGenerator {
    public void generate(VClassLoader projectClassLoader, CompileModuls compileModuls, Outbuffer ps) throws CompileException;
    public void generateInvoke(VMethod method, Outbuffer out, Value ... arguments) throws CompileException;
}
