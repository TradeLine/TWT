package org.tlsys.twt.expressions;

import org.tlsys.twt.ExecuteRecolver;
import org.tlsys.twt.TNode;
import org.tlsys.twt.links.ClassVal;
import org.tlsys.twt.links.MethodVal;

import java.util.List;

public final class StaticRef extends TExpression {

    private static final long serialVersionUID = -7983734438094979219L;
    private final TNode parent;
    private final ClassVal clazz;

    public StaticRef(TNode parent, ClassVal clazz) {
        this.parent = parent;
        this.clazz = clazz;
    }

    public ClassVal getClassRef() {
        return clazz;
    }

    @Override
    public ClassVal getResult() {
        return getClassRef();
    }

    @Override
    public TNode getParent() {
        return parent;
    }

    @Override
    public MethodVal getMethod(String name, List<ClassVal> types) {
        return ExecuteRecolver.reloveStatic(getResult(), name, types);
    }
}
