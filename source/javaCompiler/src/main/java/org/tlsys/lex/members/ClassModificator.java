package org.tlsys.lex.members;

public interface ClassModificator {

    public default MehtodSearchRequest searchMethod(MehtodSearchRequest request) {
        return request;
    }

    public default VExecute onAddExecute(VExecute execute) {
        return execute;
    }
}
