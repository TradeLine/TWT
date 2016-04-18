package org.tlsys.java.lex;

import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import org.tlsys.JavaCompiller;
import org.tlsys.lex.TConst;
import org.tlsys.lex.TNode;
import org.tlsys.lex.members.VClass;

public class JavaConst implements TConst {

    private static final long serialVersionUID = -7522483931652102757L;
    private final TNode parent;
    private transient final LiteralExpr expr;
    private VClass result;
    private Object value;
    private boolean valueGetted = false;

    public JavaConst(TNode parent, LiteralExpr expr) {
        this.parent = parent;
        this.expr = expr;
    }

    @Override
    public Object getValue() {
        if (valueGetted)
            return value;
        value = expr.getData();
        valueGetted = true;
        return value;
    }

    @Override
    public TNode getParent() {
        return parent;
    }

    @Override
    public VClass getResult() {
        if (result != null)
            return result;

        if (expr instanceof IntegerLiteralExpr) {
            result = JavaCompiller.getClassNode(parent).get().getClass("int").get();
            return result;
        }

        if (expr instanceof LongLiteralExpr) {
            result = JavaCompiller.getClassNode(parent).get().getClass("long").get();
            return result;
        }


        throw new RuntimeException("Not supported yet");
    }


}
