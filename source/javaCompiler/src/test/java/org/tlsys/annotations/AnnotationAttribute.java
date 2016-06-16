package org.tlsys.annotations;

import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Visitor;

import java.util.Map;

public class AnnotationAttribute extends Attribute
{

    Map<String, String>[] annotations;

    protected AnnotationAttribute(byte tag, int nameIndex, int length, ConstantPool constantPool)
    {
        super(tag, nameIndex, length, constantPool);
    }

    @Override
    public void accept(Visitor arg0)
    {

    }

    @Override
    public Attribute copy(ConstantPool arg0)
    {

        return null;
    }
}
