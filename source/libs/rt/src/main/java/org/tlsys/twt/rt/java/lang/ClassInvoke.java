package org.tlsys.twt.rt.java.lang;

import org.tlsys.lex.Invoke;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.GenerationContext;
import org.tlsys.twt.InvokeGenerator;

import java.io.PrintStream;

public class ClassInvoke implements InvokeGenerator {
    /*
    @Override
    public void generate(MainGenerationContext context, Class clazz, JCTree.JCExpression self, Executable method, JCTree.JCExpression[] arguments, PrintStream out) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        if(method.getName().equals("toString") || method.getName().equals("getName")) {
            out.append(context.getCodeBuilder().exp(self)).append(".fullName");
            return;
        }
        if(method.getName().equals("getSimpleName")) {
            out.append(context.getCodeBuilder().exp(self)).append(".simpleName");
            return;
        }

        throw new RuntimeException("Unknown method " + method.getName());
    }
    */

    @Override
    public boolean generate(GenerationContext ctx, Invoke invoke, PrintStream ps) throws CompileException {
        throw new RuntimeException("Not supported yet");
    }
}
