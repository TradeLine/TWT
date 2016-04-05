package org.tlsys.twt.classes;

import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
@FunctionalInterface
public interface TypeProvider {
    public ClassRecord getType();
}
