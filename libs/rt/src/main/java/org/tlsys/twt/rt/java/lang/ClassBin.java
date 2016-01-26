package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.NativeCodeGenerator;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
public class ClassBin {
    public String name;
    public TClassLoader.TypeProvider creator;
}
