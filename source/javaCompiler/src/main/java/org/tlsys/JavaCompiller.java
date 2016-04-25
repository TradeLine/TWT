package org.tlsys;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.*;
import org.tlsys.java.lex.*;
import org.tlsys.twt.TNode;
import org.tlsys.twt.expressions.TAssign;
import org.tlsys.twt.expressions.TExpression;
import org.tlsys.twt.expressions.TVarDeclare;
import org.tlsys.twt.members.*;
import org.tlsys.twt.statement.StaExpression;
import org.tlsys.twt.statement.TBlock;
import org.tlsys.twt.statement.TStatement;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

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

        addExp(AssignExpr.class, (e, p, t) -> {
            JavaAssign ja = new JavaAssign(p);
            ja.setTarget(JavaCompiller.expression(e.getTarget(), ja, null));

            ja.setValue(JavaCompiller.expression(e.getValue(), ja, ja.getTarget().getResult()));

            return ja;
        });

        addExp(NameExpr.class, (e, p, t) -> {
            Optional<TNode> node = seachUpByName(e.getName(), p, o -> true);
            if (node.isPresent()) {
                TNode n = node.get();
                if (n instanceof TVar)
                    return new JavaVarRef((TVar)n, p);

                if (n instanceof TField) {
                    TField f = (TField) n;
                    TExpression scope = Modifier.isStatic(f.getModifiers()) ? new JavaStaticRef(f.getParent(), p) : new JavaThis(f.getParent(), p);
                    JavaFieldRef jfr = new JavaFieldRef(scope, scope, f);
                    return jfr;
                }

                throw new RuntimeException("Unknown name type" + n.getClass().getName());
            }
            throw new RuntimeException("Unknown name " + e.getName());
        });
    }

    private JavaCompiller() {
    }

    private static <T extends TNode> Optional<T> seachUpByName(String name, TNode from, Predicate<TNode> validator) {
        Objects.requireNonNull(from, "From argument is NULL");
        if (from instanceof TBlock) {
            TBlock block = (TBlock)from;
            for (int i = 0; i < block.getStatementCount(); i++) {
                TStatement st = block.getStatement(i);
                if (st instanceof StaExpression) {
                    StaExpression e = (StaExpression)st;
                    if (e.getExpression() instanceof TVarDeclare) {
                        TVarDeclare dec = (TVarDeclare)e.getExpression();
                        for (TVar v : dec.getVars()) {
                            if (v.getName().equals(name)) {
                                return Optional.of((T)v);
                            }
                        }
                    }
                }
            }
            return seachUpByName(name, from.getParent(), validator);
        }

        if (from instanceof VClass) {
            VClass cl = (VClass) from;
            if (cl.getSimpleName().equals(name) && validator.test(cl))
                return Optional.of((T) cl);
            Optional<TField> f = cl.getField(name);
            if (f.isPresent())
                return Optional.of((T) f.get());
            return seachUpByName(name, cl.getParent(), validator);
        }

        if (from instanceof VPackage) {
            VPackage p = (VPackage) from;
            if (name.equals(p.getSimpleName()) && validator.test(p))
                return Optional.of((T) p);
            return seachUpByName(name, p.getParent(), validator);
        }

        if (from instanceof VExecute) {
            VExecute e = (VExecute)from;
            for (TArgument ar : e.getArguments()) {
                if (ar.getName().equals(name))
                return Optional.of((T)ar);
            }
        }
        if (from instanceof TAssign)
            return seachUpByName(name, from.getParent(), validator);
        if (from instanceof StaExpression) {
            return seachUpByName(name, from.getParent(), validator);
        }
        throw new RuntimeException("Unknown node " + from.getClass().getName());
    }

    public static <T extends VMember> T findClass(Type type, TNode from) {


        if (type instanceof VoidType) {
            TClassLoader cl = getClassNode(from).get().getClassLoader();
            return (T) cl.findClassByName("void").get();
        }

        if (type instanceof PrimitiveType) {
            TClassLoader cl = getClassNode(from).get().getClassLoader();
            PrimitiveType.Primitive pt = ((PrimitiveType) type).getType();
            switch (pt) {
                case Boolean:
                    return (T) cl.findClassByName("boolean").get();
                case Byte:
                    return (T) cl.findClassByName("byte").get();
                case Char:
                    return (T) cl.findClassByName("char").get();
                case Short:
                    return (T) cl.findClassByName("short").get();
                case Int:
                    return (T) cl.findClassByName("int").get();
                case Float:
                    return (T) cl.findClassByName("float").get();
                case Double:
                    return (T) cl.findClassByName("double").get();
                case Long:
                    return (T) cl.findClassByName("long").get();
            }
        }

        if (type instanceof ReferenceType) {
            ReferenceType rt = (ReferenceType) type;
            return findClass(rt.getType(), from);
        }

        if (type instanceof ClassOrInterfaceType) {
            ClassOrInterfaceType ci = (ClassOrInterfaceType) type;
            if (ci.getScope() != null) {

                LinkedList<ClassOrInterfaceType> list = new LinkedList<>();
                ClassOrInterfaceType t = ci;
                do {
                    list.addFirst(t);
                    t = t.getScope();
                } while (t != null);

                VMember member = (VMember) seachUp(from, e -> e instanceof VPackage && e.getParent() == null).get();
                for (ClassOrInterfaceType c : list) {
                    //member;
                    Optional<VMember> m = member.getChild(e -> {
                        if (e instanceof VPackage)
                            return ((VPackage) e).getSimpleName().equals(c.getName());
                        if (e instanceof VClass)
                            return ((VClass) e).getSimpleName().equals(c.getName());
                        System.out.println("" + c);
                        return false;
                    });
                    member = m.get();
                }
                return (T) member;
            } else {
                VClass clazz = getClassNode(from).get();
                if (clazz.getSimpleName().equals(ci.getName()))
                    return (T) clazz;
                //Optional<VClass> cls = seachUp(from, e-> e instanceof VClass);
                return (T) seachUpByName(ci.getName(), from, e -> e instanceof VClass).get();
            }
        }



        throw new RuntimeException("Not supported yet");
    }

    public static TExpression getInitValueFor(VClass type) {
        throw new RuntimeException("Not supported yet");
    }

    public static <T extends TStatement> T statement(Statement statement, TNode parentNode) {
        StatementCompiler sc = stats.get(statement.getClass());
        if (sc != null)
            return (T) sc.compile(statement, parentNode);
        throw new RuntimeException("Not supported " + statement.getClass().getName());
    }

    public static <T extends TExpression> T expression(Expression expression, TNode parent, VClass needType) {
        ExpressionCompiler sc = exps.get(expression.getClass());
        if (sc != null)
            return (T) sc.compile(expression, parent, needType);
        throw new RuntimeException("Not supported " + expression.getClass().getName());
    }

    private static <T extends TNode> Optional<T> seachUp(TNode from, Predicate<TNode> validator) {
        while (from != null) {
            if (validator.test(from))
                return Optional.of((T) from);
            from = from.getParent();
        }

        return Optional.empty();
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
                return Optional.of((VClass) from);
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
