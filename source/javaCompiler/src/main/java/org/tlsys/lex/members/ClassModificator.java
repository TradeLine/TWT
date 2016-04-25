package org.tlsys.lex.members;

import org.tlsys.twt.members.MehtodSearchRequest;
import org.tlsys.twt.members.VMethod;

public interface ClassModificator {

    public default MehtodSearchRequest searchMethod(MehtodSearchRequest request) {
        return request;
    }

    public default VMethod onAddMethod(VMethod execute) {
        return execute;
    }
}
