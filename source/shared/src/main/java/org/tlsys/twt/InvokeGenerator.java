package org.tlsys.twt;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.Outbuffer;
import org.tlsys.lex.Invoke;
import org.tlsys.lex.Value;

import java.io.PrintStream;
import java.lang.reflect.Executable;
import java.util.List;

@FunctionalInterface
public interface InvokeGenerator {
    public boolean generate(GenerationContext ctx, Invoke invoke, Outbuffer ps) throws CompileException;
}
