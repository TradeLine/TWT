package org.tlsys.ast;

import org.apache.bcel.generic.ObjectType;
import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class ClassInstanceCreation extends MethodInvocation
{

    public ClassInstanceCreation(ObjectType theType)
    {
        type= theType;
    }

    public ClassInstanceCreation(MethodDeclaration methodDecl, MethodBinding methodBinding)
    {
        super(methodDecl, methodBinding);
    }

    @Override
    public void visit(NodeVisiter visitor)
    {
        visitor.visit(this);
    }

}
