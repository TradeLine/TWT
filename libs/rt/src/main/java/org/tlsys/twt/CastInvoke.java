package org.tlsys.twt;

import com.sun.tools.javac.tree.JCTree;

import java.io.PrintStream;
import java.lang.reflect.Executable;

public class CastInvoke implements InvokeGenerator {
    @Override
    public void generate(GenContext context, Class clazz, JCTree.JCExpression self, Executable method, JCTree.JCExpression[] arguments, PrintStream stream) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        stream.append(context.getCodeBuilder().exp(arguments[0]));
    }
}
