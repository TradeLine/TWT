package org.tlsys.twt.dom;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.twt.GenContext;
import org.tlsys.twt.InvokeGenerator;

import java.io.PrintStream;
import java.lang.reflect.Executable;

public class DocumentInvoke implements InvokeGenerator {
    @Override
    public void generate(GenContext ctx, Class clazz, JCTree.JCExpression self, Executable method, JCTree.JCExpression[] arguments, PrintStream out) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        if (method.getName().equals("get")) {
            out.append("document");
            return;
        }

        if (method.getName().equals("createElement")) {
            out.append("document.createElement(").append(ctx.getCodeBuilder().exp(arguments[0])).append(")");
            return;
        }

        if (method.getName().equals("getElementById")) {
            out.append("document.getElementById(").append(ctx.getCodeBuilder().exp(arguments[0])).append(")");
            return;
        }

        throw new RuntimeException("Unknown method " + method.getName());
    }
}
