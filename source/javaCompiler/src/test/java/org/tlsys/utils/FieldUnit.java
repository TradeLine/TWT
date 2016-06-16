package org.tlsys.utils;

import org.tlsys.type.Signature;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

public class FieldUnit extends MemberUnit
{
    private static final Logger LOG = Logger.getLogger(FieldUnit.class.getName());
    public FieldUnit(Signature theSignature, ClassUnit theDeclaringClazz)
    {
        super(theSignature, theDeclaringClazz);
    }

    public void write(int depth, Writer writer) throws IOException
    {
        if (getData() == null)
            return;
        LOG.info(getIndent(depth) + getSignature());
        writer.write(getData());
    }

}
