package org.tlsys.type;

import org.tlsys.utils.ClassUnit;

/**
 * Created by Субочев Антон on 16.06.2016.
 */
public interface TypeVisitor {
    public void visit(ClassUnit clazz);
}
