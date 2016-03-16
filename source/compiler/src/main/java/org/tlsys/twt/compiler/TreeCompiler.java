package org.tlsys.twt.compiler;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import org.tlsys.TypeUtil;
import org.tlsys.lex.Context;
import org.tlsys.lex.Operation;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VClassLoader;
import org.tlsys.lex.declare.VClassNotFoundException;
import org.tlsys.twt.CompileException;

public class TreeCompiler {
    private final VClass currentClass;

    public TreeCompiler(VClass currentClass) {
        this.currentClass = currentClass;
    }

    public <T extends Operation> T op(JCTree.JCExpression tree, Context context) throws CompileException {
        return OperationCompiler.op(this, tree,context);
    }

    public Operation st(JCTree.JCStatement sta, Context context) throws CompileException {
        return StatementCompiler.st(this, sta, context);
    }

    public VClass getCurrentClass() {
        return currentClass;
    }

    public VClassLoader getClassLoader() {
        return getCurrentClass().getClassLoader();
    }

    public VClass loadClass(Type type) throws VClassNotFoundException {
        return TypeUtil.loadClass(getClassLoader(), type);
    }

    public VClass loadClass(String name) throws VClassNotFoundException {
        return getClassLoader().loadClass(name);
    }
}
