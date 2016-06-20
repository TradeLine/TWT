package org.tlsys.compiler.ast;

import org.tlsys.compiler.generators.NodeVisiter;
import org.tlsys.compiler.type.Signature;
import org.tlsys.twt.nodes.ClassReferance;

public class ClassLiteral extends Expression
{

    private Signature signature;
    private final ClassReferance referance;

    public ClassLiteral(Signature theSignature, ClassReferance referance)
    {
        signature= theSignature;
        this.referance = referance;
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

    public ClassReferance getReferance() {
        return referance;
    }
}
