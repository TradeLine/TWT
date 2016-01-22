package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.*;

@CodeGenerator(NativeCodeGenerator.class)
@JSClass
@ClassName("java.lang.ClassLoader")
@ReplaceClass(java.lang.ClassLoader.class)
public class TClassLoader/* implements JSClassLoader*/ {

    public void addClass(ClassBin classBin) {
        Script.code(this,"[",classBin.name,"]=",classBin);
        Script.code(classBin,".inited=false");
    }

    //@Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return null;
    }

    @ReplaceClass(java.lang.ClassLoader.class)
    @CodeGenerator(NativeCodeGenerator.class)
    public interface TypeProvider {
        public Class getType();
    }
}
