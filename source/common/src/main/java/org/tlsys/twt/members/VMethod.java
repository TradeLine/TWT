package org.tlsys.twt.members;

import org.tlsys.twt.links.ClassVal;
import org.tlsys.twt.links.MethodVal;

public interface VMethod extends VExecute, Named {
    public ClassVal getResult();

    @Override
    public default MethodVal asRef() {
        ClassVal[] out = new ClassVal[getArguments().size()];
        int i = 0;
        for (TArgument arg : getArguments()) {
            out[i++] = arg.getType();
        }
        return new MethodVal(getParent().asRef(), out, getName());
    }
}
