package org.tlsys.twt.compiler;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VClassNotFoundException;
import org.tlsys.twt.CompileException;

import java.util.HashMap;
import java.util.Map;

class OperationCompiler {

    public static Value getInitValueForType(VClass clazz) throws VClassNotFoundException {
        if (clazz.fullName.equals("byte") || clazz.fullName.equals("short") || clazz.fullName.equals("int") || clazz.fullName.equals("long"))
            return new Const(0, clazz);
        if (clazz.fullName.equals("float") || clazz.fullName.equals("double"))
            return new Const(0.0f, clazz);
        if (clazz.fullName.equals("boolean"))
            return new Const(false, clazz);
        if (clazz.fullName.equals("char"))
            return new Cast(clazz.getClassLoader().loadClass("int"), new Const((char) 0, clazz));
        return new Const(null, clazz);
    }

    private static interface ProcEx<V extends JCTree.JCExpression> {
        Operation proc(TreeCompiler compiller, V e, Context context) throws CompileException;
    }

    private static final Map<Class, ProcEx> exProc = new HashMap<>();

    private final VClass compileClass;

    public OperationCompiler(VClass compileClass) {
        this.compileClass = compileClass;
    }

    public static <T extends Operation> T op(TreeCompiler compiler, JCTree.JCExpression tree, Context context) throws CompileException {
        ProcEx p = exProc.get(tree.getClass());
        if (p != null)
            return (T) p.proc(compiler, tree, context);
        throw new RuntimeException("Not supported " + tree.getClass().getName() + " \"" + tree + "\"");
    }
}
