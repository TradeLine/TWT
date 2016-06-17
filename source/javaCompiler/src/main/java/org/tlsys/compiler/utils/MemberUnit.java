package org.tlsys.compiler.utils;

import org.tlsys.compiler.Compile;
import org.tlsys.compiler.type.Signature;

public abstract class MemberUnit extends Unit
{

    ClassUnit declaringClass;

    MemberUnit(Signature theSignature, ClassUnit theDeclaringClazz)
    {
        setSignature(theSignature);
        declaringClass= theDeclaringClazz;
        declaringClass.addMemberUnit(this);
    }

    public ClassUnit getDeclaringClass()
    {
        return declaringClass;
    }

    public Signature getAbsoluteSignature()
    {
        Signature s= Compile.getInstance().getSignature(declaringClass.toString(), getSignature().toString());
        return s;
    }

    public String toString()
    {
        return declaringClass.getName() + "#" + super.toString();
    }

}

