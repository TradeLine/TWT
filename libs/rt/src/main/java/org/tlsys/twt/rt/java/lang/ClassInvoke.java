package org.tlsys.twt.rt.java.lang;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.twt.GenContext;
import org.tlsys.twt.InvokeGenerator;

import java.io.PrintStream;
import java.lang.reflect.Executable;

public class ClassInvoke implements InvokeGenerator {
    @Override
    public void generate(GenContext context, Class clazz, JCTree.JCExpression self, Executable method, JCTree.JCExpression[] arguments, PrintStream out) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
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
}
