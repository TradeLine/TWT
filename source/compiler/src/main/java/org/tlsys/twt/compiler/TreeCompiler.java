package org.tlsys.twt.compiler;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.lex.Context;
import org.tlsys.lex.Operation;
import org.tlsys.lex.declare.VClass;
import org.tlsys.twt.CompileException;

/**
 * Created by Субочев Антон on 12.02.2016.
 */
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
}
