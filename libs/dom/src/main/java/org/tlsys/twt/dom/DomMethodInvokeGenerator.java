package org.tlsys.twt.dom;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.lex.Invoke;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.GenerationContext;
import org.tlsys.twt.MainGenerationContext;
import org.tlsys.twt.InvokeGenerator;

import java.io.PrintStream;
import java.lang.reflect.Executable;

public class DomMethodInvokeGenerator implements InvokeGenerator {

    /*
    @Override
    public void generate(MainGenerationContext ctx, Class clazz, JCTree.JCExpression self, Executable method, JCTree.JCExpression[] arguments, PrintStream out) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        if (method.getName().equals("setAttribute")) {
            out.append(ctx.getCodeBuilder().exp(arguments[0]));
            out.append(".setAttribute(");
            out.append(ctx.getCodeBuilder().exp(arguments[1]));
            out.append(",");
            out.append(ctx.getCodeBuilder().exp(arguments[2]));
            out.append(")");
            return;
        }

        if (method.getName().equals("getAttribute")) {
            out.append(ctx.getCodeBuilder().exp(arguments[0]));
            out.append(".getAttribute(");
            out.append(ctx.getCodeBuilder().exp(arguments[1]));
            out.append(")");
            return;
        }

        if (method.getName().equals("removeAttribute")) {
            out.append(ctx.getCodeBuilder().exp(arguments[0]));
            out.append(".removeAttribute(");
            out.append(ctx.getCodeBuilder().exp(arguments[1]));
            out.append(")");
            return;
        }

        if (method.getName().equals("getElementsByTagName")) {
            out.append(ctx.getCodeBuilder().arraySets(DomElement.class, ctx.getCodeBuilder().exp(arguments[0]) + ".getElementsByTagName(" + ctx.getCodeBuilder().exp(arguments[1]) + ")"));
            return;
        }

        if (method.getName().equals("appendChild")) {
            out.append(ctx.getCodeBuilder().exp(arguments[0]));
            out.append(".appendChild(").append(ctx.getCodeBuilder().exp(arguments[1])).append(")");
            return;
        }

        if (method.getName().equals("appendChild")) {
            out.append(ctx.getCodeBuilder().exp(arguments[0]));
            out.append(".appendChild(").append(ctx.getCodeBuilder().exp(arguments[1])).append(")");
            return;
        }

        if (method.getName().equals("removeChild")) {
            out.append(ctx.getCodeBuilder().exp(arguments[0]));
            out.append(".removeChild(").append(ctx.getCodeBuilder().exp(arguments[1])).append(")");
            return;
        }


        if (method.getName().equals("createFor")) {
            out.append("initDom(").append(ctx.getCodeBuilder().exp(arguments[0])).append(", ");
            out.append(ctx.getCodeBuilder().exp(arguments[1])).append(")");
            return;
        }

        if (method.getName().equals("childNodes")) {
            out.append(ctx.getCodeBuilder().arraySets(DomElement.class, ctx.getCodeBuilder().exp(arguments[0]) + ".childNodes"));
            return;
        }


        if (method.getName().equals("getHTML")) {
            out.append(ctx.getCodeBuilder().exp(arguments[0])).append(".innerHTML");
            return;
        }

        if (method.getName().equals("getParent")) {
            out.append(ctx.getCodeBuilder().exp(arguments[0])).append(".parentElement");
            return;
        }

        if (method.getName().equals("setHTML")) {
            out.append(ctx.getCodeBuilder().exp(arguments[0])).append(".innerHTML=")
            .append(ctx.getCodeBuilder().exp(arguments[1]));
            return;
        }

        if (method.getName().equals("query")) {
            out.append(ctx.getCodeBuilder().arraySets(DomElement.class, ctx.getCodeBuilder().exp(arguments[0]) + ".querySelectorAll(" + ctx.getCodeBuilder().exp(arguments[1]) + ")"));
            return;
        }

        throw new RuntimeException("Unknown method " + method.getName());
    }
*/
    @Override
    public boolean generate(GenerationContext ctx, Invoke invoke, PrintStream ps) throws CompileException {
        return false;
    }
}
