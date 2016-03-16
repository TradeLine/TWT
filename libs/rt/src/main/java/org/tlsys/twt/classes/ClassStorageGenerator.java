package org.tlsys.twt.classes;

import org.tlsys.lex.declare.VExecute;
import org.tlsys.twt.*;

import java.io.PrintStream;

public class ClassStorageGenerator extends NativeCodeGenerator {
    @Override
    public void generateExecute(GenerationContext context, VExecute execute, PrintStream ps, CompileModuls moduls) throws CompileException {
        generateMethodStart(context, execute, ps);
        ps.append("{return ");
        operation(context, Generator.storage, ps);
        ps.append("}");
        generateMethodEnd(context, execute, ps);
    }
}
