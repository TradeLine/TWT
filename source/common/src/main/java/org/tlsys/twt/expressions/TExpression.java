package org.tlsys.twt.expressions;

import org.tlsys.twt.ClassResolver;
import org.tlsys.twt.ExecuteRecolver;
import org.tlsys.twt.TNode;
import org.tlsys.twt.links.ClassVal;
import org.tlsys.twt.links.FieldVal;
import org.tlsys.twt.links.MethodVal;

import java.util.Arrays;
import java.util.List;

public abstract class TExpression implements TNode {

    private static final long serialVersionUID = -7623891920313276943L;

    public abstract ClassVal getResult();

    public FieldVal getField(String name) {
        return ClassResolver.resolve(getResult()).getField(name).get().asRef();
    }

    public MethodVal getMethod(String name, List<ClassVal> types) {
        return ExecuteRecolver.reloveLocal(getResult(), name, types);
    }

    public MethodVal getMethod(String name, ClassVal... types) {
        return getMethod(name, Arrays.asList(types));
    }
}
