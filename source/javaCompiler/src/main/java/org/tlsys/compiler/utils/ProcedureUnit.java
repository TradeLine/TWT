package org.tlsys.compiler.utils;

import org.tlsys.compiler.type.Signature;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.logging.Logger;

public abstract class ProcedureUnit extends MemberUnit
{
private static final Logger LOG = Logger.getLogger(ProcedureUnit.class.getName());
    private Collection<Signature> targetSignatures= new LinkedHashSet<Signature>();

    public ProcedureUnit(Signature theSignature, ClassUnit theDeclaringClazz)
    {
        super(theSignature, theDeclaringClazz);
    }

    public void addTarget(Signature targetSignature)
    {
        if (!targetSignature.toString().contains("#"))
        {
            throw new IllegalArgumentException("Signature must be field or method: " + targetSignature);
        }

        targetSignatures.add(targetSignature);
    }

    public void removeTargets()
    {
        Iterator iter= targetSignatures.iterator();
        while (iter.hasNext())
        {
            iter.next();
            iter.remove();
        }
    }

    public void write(int depth, Writer writer) throws IOException
    {
        if (getData() == null)
            return;
        LOG.info(getIndent(depth) + getSignature());
        writer.write(getData());
    }

    public String getData()
    {
        if (!declaringClass.isResolved())
            throw new RuntimeException("Class must be resolved");
        return super.getData();
    }

    public Collection<Signature> getTargetSignatures()
    {
        return targetSignatures;
    }

}