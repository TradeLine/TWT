package org.tlsys.ast;

import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class Name extends Expression {

    private String identifier;

    public Name(String newIdentifier) {

        identifier = newIdentifier;
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Name))
            return false;
        return identifier.equals(((Name) obj).identifier);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String theIdentifier) {
        identifier = theIdentifier;
    }
}
