package org.tlsys.twt.rt.java.lang.reflect;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.lex.Invoke;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.GenContext;
import org.tlsys.twt.GenerationContext;
import org.tlsys.twt.InvokeGenerator;

import java.io.PrintStream;
import java.lang.reflect.Executable;

public class ArrayInvoke implements InvokeGenerator {
    /*
    @Override
    public void generate(GenContext context, Class clazz, JCTree.JCExpression self, Executable method, JCTree.JCExpression[] arguments, PrintStream stream) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        String[] lens = new String[arguments.length-1];
        for (int i = 0; i < lens.length; i++) {
            lens[i] = context.getCodeBuilder().exp(arguments[i+1]);
        }
        stream.append(context.getCodeBuilder().arrayInit(context.getCodeBuilder().exp(arguments[0]), lens));
    }
    */

    @Override
    public boolean generate(GenerationContext ctx, Invoke invoke, PrintStream ps) throws CompileException {
        throw new RuntimeException("Not supported yet");
    }
}
