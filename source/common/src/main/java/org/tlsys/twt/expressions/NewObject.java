package org.tlsys.twt.expressions;

import org.tlsys.twt.members.TArgument;
import org.tlsys.twt.members.TConstructor;
import org.tlsys.twt.members.VClass;

import java.util.Map;

public interface NewObject extends TExpression {
    public Map<TArgument, TExpression> getArguments();

    public TConstructor getConstructor();


    @Override
    public default VClass getResult() {
        return getConstructor().getParent();
    }
}
