package org.tlsys.ast;

import org.apache.bcel.generic.Type;
import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class StringLiteral extends Expression {

    private String value;

    public StringLiteral(String theValue) {
        value = theValue;
        type = Type.STRING;
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String theValue) {
        value = theValue;
    }

}
