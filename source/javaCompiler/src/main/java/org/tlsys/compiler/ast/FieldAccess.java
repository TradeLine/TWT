package org.tlsys.compiler.ast;

import org.apache.bcel.generic.ObjectType;
import org.tlsys.compiler.Compile;
import org.tlsys.compiler.generators.AbstractVisitor;
import org.tlsys.compiler.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class FieldAccess extends Expression {

    private String name;

    private ObjectType type;

    public FieldAccess() {
    }

    public void initialize(MethodDeclaration methodDecl) {
        Compile.getInstance().addReference(methodDecl, this);
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

    public boolean isSame(Object obj) {
        if (!(obj instanceof FieldAccess))
            return false;
        FieldAccess other = (FieldAccess) obj;
        if (!name.equals(other.name))
            return false;
        if (getExpression() instanceof VariableBinding && other.getExpression() instanceof VariableBinding) {
            VariableBinding vba = (VariableBinding) getExpression();
            VariableBinding vbb = (VariableBinding) other.getExpression();
            return vba.getVariableDeclaration() == vbb.getVariableDeclaration();
        }
        return false;
    }

    public Expression getExpression() {
        return (Expression) getFirstChild();
    }

    public void setExpression(Expression expression) {
        widen(expression);
        removeChildren();
        appendChild(expression);
    }

    public String getName() {
        return name;
    }

    public void setName(String theName) {
        name = theName;
    }

    public String toString() {
        return super.toString() + " " + name;
    }

    public ObjectType getType() {
        return type;
    }

    public void setType(ObjectType theType) {
        if (type != null)
            throw new RuntimeException("Type is already set");
        type = theType;
    }

}
