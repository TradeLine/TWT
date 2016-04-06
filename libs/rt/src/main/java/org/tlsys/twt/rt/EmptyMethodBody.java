package org.tlsys.twt.rt;

import org.tlsys.Outbuffer;
import org.tlsys.lex.declare.VConstructor;
import org.tlsys.lex.declare.VExecute;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.CompileModuls;
import org.tlsys.twt.DefaultGenerator;
import org.tlsys.twt.GenerationContext;

public class EmptyMethodBody extends DefaultGenerator {
    @Override
    public void generateClass(GenerationContext context, CompileModuls.ClassRecord record, Outbuffer ps) throws CompileException {

    }

    @Override
    public void generateExecute(GenerationContext context, VExecute execute, Outbuffer ps, CompileModuls moduls) throws CompileException {
        VConstructor cons = (VConstructor) execute;
        cons.parentConstructorInvoke = null;
        operation(context, cons.getBlock(), ps);
    }
}
