package org.tlsys.compiler.utils;

import org.tlsys.compiler.type.Signature;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class Unit implements Serializable {
    private static final Logger LOG = Logger.getLogger(Unit.class.getName());
    private Signature signature;

    private String data;

    private transient boolean isTainted= false;

    private static transient Map<Integer, String> indentPerDepth= new LinkedHashMap<Integer, String>();

    public Unit()
    {
    }

    //public abstract void write(int depth, Writer writer) throws IOException;

    String getIndent(int depth)
    {
        String indent= indentPerDepth.get(depth);
        if (indent == null)
        {
            indent= "";
            for (int i= 0; i < depth; i++)
                indent+= '\t';

            indentPerDepth.put(depth, indent);
        }
        return indent;
    }

    public String toString()
    {
        return signature.toString();
    }

    public Signature getSignature()
    {
        return signature;
    }

    void setSignature(Signature theSignature)
    {
        signature= theSignature;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String theData)
    {
        data= theData;
    }

    public boolean isTainted()
    {
        return isTainted;
    }

    public void setTainted()
    {
        if (!isTainted)
            LOG.info("Taint " + this);

        isTainted= true;
    }
}
