package org.tlsys.twt.compiler;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import org.tlsys.TypeUtil;
import org.tlsys.lex.Context;
import org.tlsys.lex.Operation;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VClassLoader;
import org.tlsys.lex.declare.VClassNotFoundException;
import org.tlsys.sourcemap.SourceFile;
import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.CompileException;

public class TreeCompiler {
    private final VClass currentClass;
    private final SourceFile file;
    private final ClassCompiler.CompileContext compileContext;

    public TreeCompiler(VClass currentClass, SourceFile file, ClassCompiler.CompileContext compileContext) {
        this.currentClass = currentClass;
        this.file = file;
        this.compileContext = compileContext;
    }

    public ClassCompiler.CompileContext getCompileContext() {
        return compileContext;
    }

    public SourceFile getFile() {
        return file;
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

    public VClass loadClass(Type type, SourcePoint point) throws VClassNotFoundException {
        return TypeUtil.loadClass(getClassLoader(), type, point);
    }

    public VClass loadClass(String name, SourcePoint point) throws VClassNotFoundException {
        return getClassLoader().loadClass(name, point);
    }
}
