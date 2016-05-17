package org.tlsys.twt;

import org.tlsys.Outbuffer;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.VClassLoader;
import org.tlsys.lex.declare.VMethod;
import org.tlsys.twt.name.NameMap;

import java.io.PrintStream;

public interface MainGenerator {
    public void generate(NameMap nameMap, VClassLoader projectClassLoader, CompileModuls compileModuls, Outbuffer ps) throws CompileException;
    public void generateInvoke(NameMap nameMap, VMethod method, Outbuffer out, Value ... arguments) throws CompileException;
}
