package org.tlsys;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import org.tlsys.java.lex.JavaStaExpression;
import org.tlsys.java.lex.JavaVarDeclare;
import org.tlsys.lex.TExpression;
import org.tlsys.lex.TNode;
import org.tlsys.lex.TStatement;
import org.tlsys.lex.members.TClassLoader;
import org.tlsys.lex.members.VClass;
import org.tlsys.lex.members.VMember;

import java.util.HashMap;
import java.util.Optional;

public final class JavaCompiller {
    private JavaCompiller() {
    }

    public static VClass findClass(Type type, TNode from) {
        TClassLoader cl = getClassNode(from).get().getClassLoader();

        if (type instanceof VoidType)
            return cl.findClassByName("void").get();

        throw new RuntimeException("Not supported yet");
    }

    public static <T extends TStatement> T statement(Statement statement, TNode parentNode) {
        StatementCompiler sc = stats.get(statement.getClass());
        if (sc != null)
            return (T)sc.compile(statement, parentNode);
        throw new RuntimeException("Not supported " + statement.getClass().getName());
    }

    public static <T extends TExpression> T expression(Expression expression, TNode parent, VClass needType) {
        ExpressionCompiler sc = exps.get(expression.getClass());
        if (sc != null)
            return (T)sc.compile(expression, parent, needType);
        throw new RuntimeException("Not supported " + expression.getClass().getName());
    }

    private static final HashMap<Class, StatementCompiler> stats = new HashMap<>();
    private static final HashMap<Class, ExpressionCompiler> exps = new HashMap<>();

    static {
        addSta(ExpressionStmt.class, (s,p)->{
            JavaStaExpression jse = new JavaStaExpression(p);
            jse.setExpression(expression(s.getExpression(), jse, null));
            return jse;
        });

        addExp(VariableDeclarationExpr.class, (e,p,t)->{
            JavaVarDeclare jvd = new JavaVarDeclare(p, e);
            return jvd;
        });
    }

    private static <T extends Statement> void addSta(Class<T> clazz, StatementCompiler<T> sta) {
        stats.put(clazz, sta);
    }

    private static <T extends Expression> void addExp(Class<T> clazz, ExpressionCompiler<T> sta) {
        exps.put(clazz, sta);
    }

    @FunctionalInterface
    private interface StatementCompiler<T extends Statement> {
        TStatement compile(T sta, TNode parent);
    }

    private interface ExpressionCompiler<T extends Expression> {
        public TExpression compile(T exr, TNode parent, VClass needClass);
    }

    public static Optional<VClass> getClassNode(TNode from) {
        while (from != null) {
            if (from instanceof VClass)
                return Optional.of((VClass)from);
            from = from.getParent();
        }
        return Optional.empty();
    }
}
