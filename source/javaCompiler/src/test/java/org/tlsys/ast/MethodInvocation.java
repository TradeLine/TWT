package org.tlsys.ast;

import org.apache.bcel.generic.Type;
import org.tlsys.Compile;
import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

import java.util.List;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class MethodInvocation extends Expression {

    private Expression expression;

    public MethodDeclaration methodDecl;

    public boolean isSpecial = false;

    private MethodBinding methodBinding;

    public MethodInvocation() {
    }

    public MethodInvocation(MethodDeclaration theMethodDecl) {
        methodDecl = theMethodDecl;
    }

    public MethodInvocation(MethodDeclaration theMethodDecl, MethodBinding theMethodBinding) {
        methodDecl = theMethodDecl;
        setMethodBinding(theMethodBinding);
    }

    public Type getTypeBinding() {
        if (methodBinding == null)
            return super.getTypeBinding();
        return methodBinding.getReturnType();
    }

    public boolean isSuper(String currentClassName) {
        if (!isSpecial)
            return false;

        if (methodBinding.isConstructor()) {
            if (!(getExpression() instanceof ThisExpression))
                return false;
        }

        String name = methodBinding.getDeclaringClass().getClassName();
        if (currentClassName.equals(name))
            return false;

        return true;
    }

    public List<ASTNode> getArguments() {
        ASTNodeStack stack = new ASTNodeStack();
        ASTNode node = getFirstChild();
        if (expression != null) {
            node = node.getNextSibling();
        }

        while (node != null) {
            stack.add(node);
            node = node.getNextSibling();
        }

        return stack;

    }

    public void addArgument(Expression argument) {
        widen(argument);
        appendChild(argument);
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression targetExpression) {
        if (expression != null) {
            throw new RuntimeException("Expression is already set");
        }
        expression = targetExpression;
        widen(expression);
        insertBefore(expression, getFirstChild());
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

    public MethodBinding getMethodBinding() {
        return methodBinding;
    }

    public void setMethodBinding(MethodBinding theMethodBinding) {
        methodBinding = theMethodBinding;
        Compile.getInstance().addReference(methodDecl, this);
    }
}
