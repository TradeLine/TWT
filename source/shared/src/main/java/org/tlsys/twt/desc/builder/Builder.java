package org.tlsys.twt.desc.builder;

public interface Builder<RESULT, PARENT> {
    PARENT build();
    RESULT result();
}
