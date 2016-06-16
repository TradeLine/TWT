package org.tlsys.type;

import org.tlsys.utils.ClassUnit;

import java.util.Collection;
import java.util.LinkedHashSet;

public class TypeCollector implements TypeVisitor
{

    public Collection<ClassUnit> collectedTypes= new LinkedHashSet<ClassUnit>();

    public void visit(ClassUnit clazz)
    {
        collectedTypes.add(clazz);
    }
}
