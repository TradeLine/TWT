package org.tlsys.twt.rt.java.lang;

import org.tlsys.Outbuffer;
import org.tlsys.lex.Invoke;
import org.tlsys.lex.StaticRef;
import org.tlsys.lex.declare.VClass;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.GenerationContext;
import org.tlsys.twt.InvokeGenerator;

import java.io.PrintStream;

public class ObjectInvokeAdapter implements InvokeGenerator {
    @Override
    public boolean generate(GenerationContext generationContext, Invoke invoke, Outbuffer printStream) throws CompileException {
        VClass objectClass = invoke.getMethod().getParent();
        return generationContext.getGenerator(objectClass).operation(generationContext, new Invoke(objectClass.getMethod("getClassOfObject", objectClass), new StaticRef(objectClass)).addArg(invoke.getSelf()), printStream);
    }
}
