package org;

import org.tlsys.ast.*;
import org.tlsys.ast.Block;
import org.tlsys.generators.NodeVisiter;

public class BlockVisiter implements NodeVisiter {
    private final int level;
    private final MethodVisiter methodVisiter;

    public BlockVisiter(int level, MethodVisiter methodVisiter) {
        this.level = level;
        this.methodVisiter = methodVisiter;
    }

    @Override
    public void visit(ASTNode node) {
        throw new RuntimeException("Unknown node " + node);
    }

    private void drawLevel(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("\t\t");
        }
        System.out.print(sb.toString());
    }

    @Override
    public void visit(Block node) {
        drawLevel(level);
        System.out.println("{");
        if (level == 0) {
            for (VariableDeclaration vd : methodVisiter.getMethodDeclaration().getLocalVariables()) {
                drawLevel(level+1);
                System.out.println("var " + vd.getName()+";");
            }
        }
        ASTNode n = node.getFirstChild();
        while (n != null) {
            if (n.getClass() == Block.class) {
                BlockVisiter bv = new BlockVisiter(level + 1, methodVisiter);
                drawLevel(level);
                n.visit(bv);
                n = n.getNextSibling();
            } else {
                drawLevel(level + 1);
                n.visit(this);
                System.out.println("// LINE" + methodVisiter.getLineNumberCursor().getLineNumber(n));
                n = n.getNextSibling();
            }
        }
        drawLevel(level);
        System.out.println("}");
    }

    @Override
    public void visit(MethodInvocation node) {
        /*
        if (node.isSpecial)
            System.out.print("SPECIAL ");
        else */{
            if (node.getExpression() == null) {
                //System.out.print("STATIC ");
                System.out.print(node.getMethodBinding().getDeclaringClass().getClassName()+".");
            } //else
                //System.out.print("VIRTUAL ");
        }
        if (node.getExpression() != null) {
            System.out.print(" ");
            node.getExpression().visit(this);
            System.out.print(".");
        }

        System.out.print(node.getMethodBinding().getName()+"(");
        boolean first = true;
        for (ASTNode n : node.getArguments()) {
            if (!first) {
                System.out.print(", ");

            } else
                first = false;
            n.visit(this);
        }
        System.out.print(")");
    }

    @Override
    public void visit(VariableBinding node) {
        System.out.print(node.getName());
    }

    @Override
    public void visit(ArrayCreation node) {
        System.out.print("visit(ArrayCreation node) " + node);
    }

    @Override
    public void visit(ArrayAccess node) {
        if (node.getChildCount() < 2)
            throw new RuntimeException("!!! ERROR !!!");
        ASTNode el = node.getFirstChild();
        el.visit(this);
        el = el.getNextSibling();
        {

            System.out.print("[");
            el.visit(this);
            System.out.print("]");
            el = el.getNextSibling();
        } while (el != null);
    }

    @Override
    public void visit(ThisExpression node) {
        System.out.print("this[" + node.getType().getSignature()+"]");
    }

    @Override
    public void visit(DoStatement node) {
        System.out.print("visit(DoStatement node)");
    }

    @Override
    public void visit(WhileStatement node) {
        System.out.print("visit(WhileStatement node)");
    }

    @Override
    public void visit(IfStatement node) {
        System.out.print("visit(IfStatement node)");
    }

    @Override
    public void visit(TryStatement node) {
        System.out.print("visit(TryStatement node)");
    }

    @Override
    public void visit(InfixExpression node) {
        if (node.getChildCount() != 2)
            throw new RuntimeException("!!! ERROR !!!");
        node.getFirstChild().visit(this);
        System.out.print(node.getOperator().toString());
        node.getLastChild().visit(this);
    }

    @Override
    public void visit(PrefixExpression node) {
        System.out.print("visit(PrefixExpression node)");
    }

    @Override
    public void visit(PostfixExpression node) {
        System.out.print("visit(PostfixExpression node)");
    }

    @Override
    public void visit(SwitchStatement node) {
        System.out.print("visit(SwitchStatement node)");
    }

    @Override
    public void visit(SwitchCase node) {
        System.out.print("visit(SwitchCase node)");
    }

    @Override
    public void visit(CatchClause node) {
        System.out.print("visit(CatchClause node)");
    }



    @Override
    public void visit(StringLiteral node) {
        //System.out.print("visit(StringLiteral node)");
        System.out.print("\"" + node.getValue() + "\"");
    }

    @Override
    public void visit(ClassLiteral node) {
        System.out.print("visit(ClassLiteral node)");
    }

    @Override
    public void visit(NullLiteral node) {
        System.out.print("visit(NullLiteral node)");
    }

    @Override
    public void visit(ClassInstanceCreation node) {
        System.out.print("new " + node.getTypeBinding().getSignature());
        System.out.print("(");
        boolean first = true;
        for (ASTNode n : node.getArguments()) {
            if (!first)
                System.out.print(", ");
            else
                first = false;
            n.visit(this);
        }
        System.out.print(")");
    }

    @Override
    public void visit(ArrayInitializer node) {
        System.out.print("visit(ArrayInitializer node)");
    }



    @Override
    public void visit(VariableDeclaration node) {
        System.out.print("visit(VariableDeclaration node)");
    }





    @Override
    public void visit(FieldAccess node) {
        if (node.getExpression() == null)
            System.out.print(node.getType().getSignature());
        else
            node.getExpression().visit(this);
        System.out.print("."+node.getName());
    }

    @Override
    public void visit(BreakStatement node) {
        System.out.print("visit(BreakStatement node)");
    }

    @Override
    public void visit(ContinueStatement node) {
        System.out.print("visit(ContinueStatement node)");
    }

    @Override
    public void visit(CastExpression node) {
        System.out.print("visit(CastExpression node)");
    }

    @Override
    public void visit(BooleanLiteral node) {
        System.out.print("visit(BooleanLiteral node)");
    }

    @Override
    public void visit(ThrowStatement node) {
        System.out.print("visit(ThrowStatement node)");
    }

    @Override
    public void visit(Name node) {
        System.out.print("visit(Name node)");
    }

    @Override
    public void visit(InstanceofExpression node) {
        System.out.print("visit(InstanceofExpression node)");
    }

    @Override
    public void visit(ConditionalExpression node) {
        System.out.print("visit(ConditionalExpression node)");
    }

    @Override
    public void visit(SynchronizedBlock node) {
        System.out.print("visit(SynchronizedBlock node)");
    }

    @Override
    public void visit(PrimitiveCast node) {
        System.out.print("visit(PrimitiveCast node)");
    }

    @Override
    public void visit(ReturnStatement node) {
        System.out.print("RETURN");
        if (node.getExpression() != null) {
            System.out.print(" ");
            node.getExpression().visit(this);
        }
    }

    @Override
    public void visit(Assignment node) {
        if (node.getChildCount() != 2)
            throw new RuntimeException("!!! ERROR !!!");
        node.getFirstChild().visit(this);
        System.out.print(node.getOperator().toString());
        node.getLastChild().visit(this);
    }

    @Override
    public void visit(NumberLiteral node) {
        System.out.print(node.getValue());
    }
}
