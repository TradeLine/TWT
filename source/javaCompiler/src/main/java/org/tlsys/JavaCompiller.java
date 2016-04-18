package org.tlsys;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import org.tlsys.java.lex.JavaBlock;
import org.tlsys.java.lex.JavaConst;
import org.tlsys.java.lex.JavaStaExpression;
import org.tlsys.java.lex.JavaVarDeclare;
import org.tlsys.lex.TExpression;
import org.tlsys.lex.TNode;
import org.tlsys.lex.TStatement;
import org.tlsys.lex.members.TClassLoader;
import org.tlsys.lex.members.VClass;

import java.util.HashMap;
import java.util.Optional;

public final class JavaCompiller {
    private static final HashMap<Class, StatementCompiler> stats = new HashMap<>();
    private static final HashMap<Class, ExpressionCompiler> exps = new HashMap<>();

    static {
        addSta(ExpressionStmt.class, (s, p) -> {
            JavaStaExpression jse = new JavaStaExpression(p);
            jse.setExpression(expression(s.getExpression(), jse, null));
            return jse;
        });

        addExp(VariableDeclarationExpr.class, (e, p, t) -> {
            JavaVarDeclare jvd = new JavaVarDeclare(p, e);
            return jvd;
        });

        addSta(BlockStmt.class, (e, p) -> {
            JavaBlock block = new JavaBlock(p);
            for (Statement s : e.getStmts()) {
                TStatement ss = JavaCompiller.statement(s, block);
                if (ss != null)
                    block.add(ss);
            }
            return block;
        });

        ExpressionCompiler<LiteralExpr> constCompiller = (e, p, t) -> {
            return new JavaConst(p, e);
        };

        addExp(IntegerLiteralExpr.class, constCompiller);
        addExp(LongLiteralExpr.class, constCompiller);
    }

    private JavaCompiller() {
    }

    public static VClass findClass(Type type, TNode from) {
        TClassLoader cl = getClassNode(from).get().getClassLoader();

        if (type instanceof VoidType)
            return cl.findClassByName("void").get();

        if (type instanceof PrimitiveType) {
            PrimitiveType.Primitive pt = ((PrimitiveType) type).getType();
            switch (pt) {
                case Boolean:
                    return cl.findClassByName("boolean").get();
                case Byte:
                    return cl.findClassByName("byte").get();
                case Char:
                    return cl.findClassByName("char").get();
                case Short:
                    return cl.findClassByName("short").get();
                case Int:
                    return cl.findClassByName("int").get();
                case Float:
                    return cl.findClassByName("float").get();
                case Double:
                    return cl.findClassByName("double").get();
                case Long:
                    return cl.findClassByName("long").get();
            }
        }

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

    private static <T extends Statement> void addSta(Class<T> clazz, StatementCompiler<T> sta) {
        stats.put(clazz, sta);
    }

    private static <T extends Expression> void addExp(Class<? extends T> clazz, ExpressionCompiler<? extends T> sta) {
        exps.put(clazz, sta);
    }

    public static Optional<VClass> getClassNode(TNode from) {
        while (from != null) {
            if (from instanceof VClass)
                return Optional.of((VClass)from);
            from = from.getParent();
        }
        return Optional.empty();
    }

    @FunctionalInterface
    private interface StatementCompiler<T extends Statement> {
        TStatement compile(T sta, TNode parent);
    }

    private interface ExpressionCompiler<T extends Expression> {
        public TExpression compile(T exr, TNode parent, VClass needClass);
    }
}
