package org.tlsys.generators;

import org.tlsys.ast.*;
import org.tlsys.ast.Block;

public interface NodeVisiter {
    public void visit(ASTNode node);
    public default void visit(PrimitiveCast cast)
    {
        visit((ASTNode) cast);
    }
    public default void visit(Block node)
    {
        visit((ASTNode) node);
    }
    public default void visit(MethodInvocation node)
    {
        visit((ASTNode) node);
    }
    public default void visit(ThisExpression node)
    {
        visit((ASTNode) node);
    }
    public default void visit(BreakStatement node)
    {
        visit((ASTNode) node);
    }
    public default void visit(ContinueStatement node)
    {
        visit((ASTNode) node);
    }
    public default void visit(CastExpression node)
    {
        visit((ASTNode) node);
    }
    public default void visit(TypeDeclaration node)
    {
        visit((ASTNode) node);
    }

    public default void visit(MethodDeclaration node)
    {
        visit((ASTNode) node);
    }

    public default void visit(DoStatement node)
    {
        visit((ASTNode) node);
    }

    public default void visit(WhileStatement node)
    {
        visit((ASTNode) node);
    }

    public default void visit(IfStatement node)
    {
        visit((ASTNode) node);
    }

    public default void visit(TryStatement node)
    {
        visit((ASTNode) node);
    }



    public default void visit(InfixExpression node)
    {
        visit((ASTNode) node);
    }

    public default void visit(PrefixExpression node)
    {
        visit((ASTNode) node);
    }

    public default void visit(PostfixExpression node)
    {
        visit((ASTNode) node);
    }

    public default void visit(SwitchStatement node)
    {
        visit((ASTNode) node);
    }

    public default void visit(SwitchCase node)
    {
        visit((ASTNode) node);
    }

    public default void visit(CatchClause node)
    {
        visit((ASTNode) node);
    }

    public default void visit(ReturnStatement node)
    {
        visit((ASTNode) node);
    }

    public default void visit(Assignment node)
    {
        visit((ASTNode) node);
    }

    public default void visit(NumberLiteral node)
    {
        visit((ASTNode) node);
    }

    public default void visit(StringLiteral node)
    {
        visit((ASTNode) node);
    }

    public default void visit(ClassLiteral node)
    {
        visit((ASTNode) node);
    }

    public default void visit(NullLiteral node)
    {
        visit((ASTNode) node);
    }



    public default void visit(ClassInstanceCreation node)
    {
        visit((ASTNode) node);
    }

    public default void visit(ArrayInitializer node)
    {
        visit((ASTNode) node);
    }

    public default void visit(ArrayCreation node)
    {
        visit((ASTNode) node);
    }

    public default void visit(ArrayAccess node)
    {
        visit((ASTNode) node);
    }

    public default void visit(VariableDeclaration node)
    {
        visit((ASTNode) node);
    }

    public default void visit(VariableBinding node)
    {
        visit((ASTNode) node);
    }

    public default void visit(FieldAccess node)
    {
        visit((ASTNode) node);
    }



    public default void visit(BooleanLiteral node)
    {
        visit((ASTNode) node);
    }

    public default void visit(ThrowStatement node)
    {
        visit((ASTNode) node);
    }

    public default void visit(Name node)
    {
        visit((ASTNode) node);
    }

    public default void visit(InstanceofExpression node)
    {
        visit((ASTNode) node);
    }

    public default void visit(ConditionalExpression node)
    {
        visit((ASTNode) node);
    }

    public default void visit(SynchronizedBlock node)
    {
        visit((ASTNode) node);
    }
}
