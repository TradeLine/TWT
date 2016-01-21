package org.tlsys.twt.desc.builder;

public interface Builder<RESULT, PARENT> {
    public PARENT build();
    public RESULT result();
}
