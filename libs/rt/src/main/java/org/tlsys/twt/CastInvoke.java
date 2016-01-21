package org.tlsys.twt;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.lex.Invoke;
import org.tlsys.lex.Value;

import java.io.PrintStream;
import java.lang.reflect.Executable;
import java.util.List;

public class CastInvoke implements InvokeGenerator {
    /*
    @Override
    public void generate(GenContext context, Class clazz, JCTree.JCExpression self, Executable method, JCTree.JCExpression[] arguments, PrintStream stream) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        stream.append(context.getCodeBuilder().exp(arguments[0]));
    }
    */

    @Override
    public boolean generate(GenerationContext ctx, Invoke invoke, PrintStream ps) throws CompileException {
        throw new RuntimeException("Not supported");
    }
}
