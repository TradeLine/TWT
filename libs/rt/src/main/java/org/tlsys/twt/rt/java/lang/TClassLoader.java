package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.*;

@CodeGenerator(ClassLoaderCodeGenerator.class)
@JSClass
@ClassName("java.lang.ClassLoader")
@ReplaceClass(java.lang.ClassLoader.class)
public class TClassLoader/* implements JSClassLoader*/ {

    public void addClass(String className, Object classObject) {
        Script.code(this,".",className,"=",classObject);
    }

    //@Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return null;
    }
}
