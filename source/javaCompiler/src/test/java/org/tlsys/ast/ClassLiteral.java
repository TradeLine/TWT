package org.tlsys.ast;

import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;
import org.tlsys.type.Signature;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class ClassLiteral extends Expression
{

    private Signature signature;

    public ClassLiteral(Signature theSignature)
    {
        signature= theSignature;
    }

    @Override
    public void visit(NodeVisiter visitor)
    {
        visitor.visit(this);
    }

    public Signature getSignature()
    {
        return signature;
    }

}
