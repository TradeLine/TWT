package org.tlsys.twt.rt;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.lex.Operation;
import org.tlsys.lex.declare.VExecute;
import org.tlsys.twt.*;

import java.io.PrintStream;
import java.lang.reflect.Executable;

public class EmptyMethodBody implements ICodeGenerator {
    @Override
    public void generateClass(GenerationContext context, CompileModuls.ClassRecord record, PrintStream ps) throws CompileException {

    }

    @Override
    public boolean operation(GenerationContext context, Operation operation, PrintStream out) throws CompileException {
        return false;
    }

    @Override
    public void generateExecute(GenerationContext context, VExecute execute, PrintStream ps) throws CompileException {
    }
}
