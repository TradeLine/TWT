package org.tlsys.twt.rt.java.lang;

import org.tlsys.lex.Operation;
import org.tlsys.lex.declare.VExecute;
import org.tlsys.twt.*;

import java.io.PrintStream;

/**
 * Генератор тела функции для TClass.initArrayClass
 */
public class GenArrayClassCreateMethod extends NativeCodeGenerator {
    //TODO дописать генератор тела функции для создания класса-массива

    @Override
    public void generateExecute(GenerationContext context, VExecute execute, PrintStream ps) throws CompileException {
        generateMethodStart(context, execute, ps);
        ps.append("{}");
        generateMethodEnd(context, execute, ps);
    }
}
