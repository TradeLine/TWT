package org.tlsys.twt;

import com.sun.tools.javac.tree.JCTree;

import java.io.PrintStream;

public interface ExpProc {
    public String proc(GenContext context, Class clazz, JCTree.JCExpression scope, JCTree.JCExpression exp) throws NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException;
    public void proc(GenContext context, Class clazz, JCTree.JCExpression scope, JCTree.JCExpression exp, PrintStream ps) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException;
}
