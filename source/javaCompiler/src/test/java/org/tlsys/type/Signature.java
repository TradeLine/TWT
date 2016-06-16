package org.tlsys.type;

import org.tlsys.Compile;

import java.io.Serializable;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class Signature implements Serializable
{

    private String signatureString;

    private int id;

    public Signature(String theSignatureString, int theId)
    {
        signatureString= theSignatureString;
        id= theId;
    }

    public int hashCode()
    {
        return signatureString.hashCode();
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof Signature)
        {
            return signatureString.equals(((Signature) obj).signatureString);
        }
        return false;
    }

    public String toString()
    {
        return signatureString;
    }

    public boolean isClass()
    {
        return signatureString.indexOf('#') == -1;
    }

    public boolean isArrayType()
    {
        return isClass() && signatureString.startsWith("[");
    }

    public boolean isConstructor()
    {
        return signatureString.startsWith("<init>");
    }

    public boolean isMethod()
    {
        return !isConstructor() && signatureString.indexOf('(') != -1;
    }

    public boolean isField()
    {
        return !isClass() && signatureString.indexOf('(') == -1;
    }

    public String className()
    {
        String array[]= signatureString.split("#");

        return array[0];
    }

    public String relativeSignature()
    {
        String array[]= signatureString.split("#");
        if (array.length != 2)
        {
            throw new RuntimeException("Not a method signature: " + this);
        }
        return array[1];
    }

    public Signature relative()
    {
        return Compile.getInstance().getSignature(relativeSignature());
    }

    public int getId()
    {
        return id;
    }

    public String getCommentedId()
    {
        StringBuffer sb= new StringBuffer();
        sb.append(String.valueOf(getId()));
            sb.append(" /*");
            sb.append(toString());
            sb.append("*/");
        return sb.toString();
    }
}