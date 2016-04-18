package org.tlsys.java.lex;

import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import org.tlsys.JavaCompiller;
import org.tlsys.lex.TNode;
import org.tlsys.lex.TVar;
import org.tlsys.lex.TVarDeclare;
import org.tlsys.lex.members.VClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class JavaVarDeclare implements TVarDeclare {

    private static final long serialVersionUID = 6324423789569347577L;
    transient final VariableDeclarationExpr declarationExpr;
    private final TNode parent;
    private List<TVar> var;
    private VClass result;

    public JavaVarDeclare(TNode parent, VariableDeclarationExpr declarationExpr) {
        this.parent = parent;
        this.declarationExpr = declarationExpr;
    }

    @Override
    public List<TVar> getVars() {
        if (var != null)
            return var;



        Vector<TVar> vars = new Vector<>();
        declarationExpr.getVars().parallelStream().forEach(e->{
            vars.add(new JavaVar(e, this, getResult()));
        });

        var = new ArrayList<>();
        this.var.addAll(vars);
        return var;
    }

    @Override
    public TNode getParent() {
        return parent;
    }

    @Override
    public VClass getResult() {
        if (result != null)
            return result;
        result = JavaCompiller.findClass(declarationExpr.getType(), this);
        return result;
    }
}
