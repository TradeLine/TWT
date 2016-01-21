package org.tlsys.twt;

import com.sun.tools.javac.tree.JCTree;

import java.io.PrintStream;
import java.lang.reflect.Executable;

public interface MethodBodyGenerator {
    void gen(GenContext context, JCTree.JCMethodDecl decl, Executable method, PrintStream out) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException;
}
