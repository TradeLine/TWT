package org.tlsys.twt.members;

import org.tlsys.twt.ConstructorVal;
import org.tlsys.twt.ExecuteVal;
import org.tlsys.twt.links.ClassVal;

public interface TConstructor extends VExecute {
    @Override
    default ExecuteVal asRef() {
        ClassVal[] out = new ClassVal[getArguments().size()];
        int i = 0;
        for (TArgument arg : getArguments()) {
            out[i++] = arg.getType();
        }
        return new ConstructorVal(getParent().asRef(), out);
    }
}
