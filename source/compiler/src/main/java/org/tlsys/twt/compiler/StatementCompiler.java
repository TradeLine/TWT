package org.tlsys.twt.compiler;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.ArrayClass;
import org.tlsys.lex.declare.VBlock;
import org.tlsys.lex.declare.VMethod;
import org.tlsys.twt.CompileException;

import java.util.HashMap;
import java.util.Map;

class StatementCompiler {

    private static final Map<Class, ProcSt> stProc = new HashMap<>();

    public static Operation st(TreeCompiler compiler, JCTree.JCStatement sta, Context context) throws CompileException {
        ProcSt p = stProc.get(sta.getClass());
        if (p != null)
            return p.proc(compiler, sta, context);
        throw new RuntimeException("Not supported " + sta.getClass().getName() + " \"" + sta + "\"");
    }

    private static interface ProcSt<V extends JCTree.JCStatement> {
        Operation proc(TreeCompiler compiller, V e, Context context) throws CompileException;
    }

    public static VMethod createBrig(VMethod from, VMethod to) {
        VMethod rep = new VMethod(to.getParent(), null, null);
        rep.setReplace(from);
        rep.arguments.addAll(from.arguments);
        rep.block = new VBlock(rep);
        rep.alias = from.alias;

        Invoke inv = new Invoke(to, new This(to.getParent()));
        inv.arguments.addAll(rep.arguments);
        inv.returnType = to.returnType;

        if (!(from.returnType instanceof ArrayClass) && from.returnType.isThis("void")) {
            rep.block.add(inv);
        } else {
            rep.block.add(new Return(inv));
        }
        return rep;
    }

}
