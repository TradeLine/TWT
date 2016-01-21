package org.tlsys.twt.rt.java.lang;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.twt.GenContext;
import org.tlsys.twt.InvokeGenerator;
import org.tlsys.twt.rt.Tchar;

import java.io.PrintStream;
import java.lang.reflect.Executable;

public class StringInvoke implements InvokeGenerator {
    @Override
    public void generate(GenContext ctx, Class clazz, JCTree.JCExpression self, Executable method, JCTree.JCExpression[] arguments, PrintStream ps) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        if (method.getName().equals("charAt")) {
            String t = ctx.getCodeBuilder().exp(self) + ".charAt(" + ctx.getCodeBuilder().exp(arguments[0]) + ")";
            ps.append(ctx.getCodeBuilder().newInst(Tchar.class, Tchar.class.getConstructor(String.class), t));
            return;
        }

        if (method.getName().equals("equals")) {
            ps.append("(").append(ctx.getCodeBuilder().exp(self)).append("==").append(ctx.getCodeBuilder().exp(arguments[0])).append(")");
            return;
        }

        if (method.getName().equals("length")) {
            ps.append(ctx.getCodeBuilder().exp(self) + ".length");
            return;
        }

        if (method.getName().equals("indexOf")) {
            ps.append(ctx.getCodeBuilder().exp(self) + ".indexOf(");
            ps.append(ctx.getCodeBuilder().exp(arguments[0]));
            if (arguments.length>1)
                ps.append(",").append(ctx.getCodeBuilder().exp(arguments[1]));
            ps.append(")");
            return;
        }

        if (method.getName().equals("lastIndexOf")) {
            ps.append(ctx.getCodeBuilder().exp(self) + ".lastIndexOf(");
            ps.append(ctx.getCodeBuilder().exp(arguments[0]));
            if (arguments.length>1)
                ps.append(",").append(ctx.getCodeBuilder().exp(arguments[1]));
            ps.append(")");
            return;
        }

        if (method.getName().equals("isEmpty")) {
            ps.append("(").append(ctx.getCodeBuilder().exp(self) + ".length==0)");
            return;
        }

        if (method.getName().equals("substring")) {
            ps.append(ctx.getCodeBuilder().exp(self) + ".substring(");
            ps.append(ctx.getCodeBuilder().exp(arguments[0]));
            if (arguments.length>1)
                ps.append(",").append(ctx.getCodeBuilder().exp(arguments[1]));
            ps.append(")");
            return;
        }

        if (method.getName().equals("valueOf")) {
            ps.append("(").append(ctx.getCodeBuilder().exp(arguments[0])).append("==null?'null':").append(ctx.getCodeBuilder().exp(arguments[0])).append(".toString())");
            return;
        }

        throw new RuntimeException("Unknown method " + method.getName());
    }
}
