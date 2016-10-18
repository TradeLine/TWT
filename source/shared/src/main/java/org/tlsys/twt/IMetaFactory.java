package org.tlsys.twt;

import org.tlsys.twt.desc.ClassDesc;

import java.io.PrintStream;

public interface IMetaFactory {
    void genMeta(GenContext context, ClassDesc desc, PrintStream stream) throws NoSuchMethodException, ClassNotFoundException;
}
