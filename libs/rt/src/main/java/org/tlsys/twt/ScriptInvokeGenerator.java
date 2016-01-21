package org.tlsys.twt;

//import com.sun.tools.javac.tree.JCTree;
import org.tlsys.twt.annotations.NotCompile;

import java.io.PrintStream;
import java.lang.reflect.Executable;

@NotCompile
public class ScriptInvokeGenerator /*implements InvokeGenerator*/ {
    /*
    @Override
    public void generate(GenContext context, Class clazz, JCTree.JCExpression self, Executable method, JCTree.JCExpression[] arguments, PrintStream stream) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        if(method.getName().equals("code")) {
            for (JCTree.JCExpression e : arguments) {
                if (e instanceof JCTree.JCLiteral) {
                    JCTree.JCLiteral l = (JCTree.JCLiteral) e;
                    stream.append(String.valueOf(l.getValue()));
                    continue;
                }
                stream.append(context.getCodeBuilder().exp(e));
                //context.getExpProc().proc(context, currentClass, e, stream);
            }

            return;
        }

        if (method.getName().equals("isUndefined")) {
            stream.append("(").append(context.getCodeBuilder().exp(arguments[0])).append("==undefined").append(")");
            return;
        }

        if (method.getName().equals("typeOf")) {
            stream.append("(typeof ").append(context.getCodeBuilder().exp(arguments[0])).append(")");
            return;
        }
        throw new RuntimeException("Unknown method " + method.getName());
    }
    */
}
