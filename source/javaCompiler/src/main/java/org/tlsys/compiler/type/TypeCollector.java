package org.tlsys.compiler.type;

import org.tlsys.compiler.utils.ClassUnit;

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
