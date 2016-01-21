package org.tlsys.twt.rt;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.twt.GenContext;
import org.tlsys.twt.MethodBodyGenerator;

import java.io.PrintStream;
import java.lang.reflect.Executable;

public class EmptyMethodBody implements MethodBodyGenerator {

    @Override
    public void gen(GenContext context, JCTree.JCMethodDecl decl, Executable method, PrintStream out) throws NoSuchMethodException, ClassNotFoundException {
        out.append("{}");
    }
}
