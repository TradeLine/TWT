package org.tlsys.compiler.ast;

import org.apache.bcel.generic.Type;
import org.tlsys.compiler.generators.AbstractVisitor;
import org.tlsys.compiler.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class VariableBinding extends Expression implements Assignable {
    private boolean field;

    private VariableDeclaration decl;

    private boolean isTemporary = false;

    public static boolean isBoolean(Expression expr) {
        if (expr == null || !(expr instanceof VariableBinding))
            return false;
        if (((VariableBinding) expr).getVariableDeclaration().getType() != Type.BOOLEAN)
            return false;
        return true;
    }

    public VariableBinding(VariableDeclaration theDecl) {
        super();
        decl = theDecl;
        decl.vbs.add(this);
        setTypeBinding(theDecl.getType());
    }

    public Type getType() {
        return decl.getType();
    }

    public Object clone() {

        VariableBinding other = (VariableBinding) super.clone();
        decl.vbs.add(other);
        return other;
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

    public boolean isSame(Object other) {
        if (other == null || !(other instanceof VariableBinding))
            return false;
        return decl == ((VariableBinding) other).decl;
    }

    public String getName() {
        return decl.getName();
    }

    public boolean isField() {
        return field;
    }

    public void setField(boolean theField) {
        field = theField;
    }

    public VariableDeclaration getVariableDeclaration() {
        return decl;
    }

    public String toString() {
        return super.toString() + ' ' + decl.getName();
    }

    public boolean isTemporary() {
        return isTemporary;
    }

    public void setTemporary(boolean isTemporary) {
        this.isTemporary = isTemporary;
    }
}
