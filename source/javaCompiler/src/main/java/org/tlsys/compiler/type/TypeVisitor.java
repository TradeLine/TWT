package org.tlsys.compiler.type;

import org.tlsys.compiler.utils.ClassUnit;

/**
 * Created by Субочев Антон on 16.06.2016.
 */
public interface TypeVisitor {
    public void visit(ClassUnit clazz);
}
