package org.tlsys.twt;

import com.sun.tools.javac.tree.JCTree;

import java.io.PrintStream;
import java.lang.reflect.Executable;

@FunctionalInterface
public interface InvokeGenerator {
    public void generate(GenContext context, Class clazz, JCTree.JCExpression self, Executable method, JCTree.JCExpression[] arguments, PrintStream stream) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException;
}
