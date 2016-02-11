package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.*;
import org.tlsys.twt.NativeCodeGenerator;

//@CodeGenerator(NativeCodeGenerator.class)
@JSClass
@ClassName("java.lang.ClassLoader")
@ReplaceClass(java.lang.ClassLoader.class)
public class TClassLoader/* implements JSClassLoader*/ {

    public void addClass(String name, TClass clazz) {
        Script.code(this,"[",name,"]=",clazz);
    }

    //@Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return null;
    }

    public Class<?> getClass(Object object) {
        TClass clazz = CastUtil.cast(object);
        if (!clazz.isInited())
            clazz.init();
        return CastUtil.cast(clazz);
    }
}
