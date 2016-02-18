package org.tlsys.twt.classes;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.rt.java.lang.TClass;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
public class ClassStorage {
    public ClassRecord add(ClassRecord cr) {
        Script.code(this,"[",cr.getJsName(),"]=",cr);
        Script.code(this,"[",cr.getName(),"]=",cr);
        return cr;
    }

    public Class get(Object o) {
        if (Script.isPrototypeOf(o, ClassRecord.class)) {
            ClassRecord cr = CastUtil.cast(o);
            TClass clazz = new TClass(cr.getJsName());
            Script.code(this, "[", cr.getJsName(),"]=",clazz);
            Script.code(this,"[",cr.getName(),"]=",clazz);
            clazz.initFor(cr);
            return CastUtil.cast(clazz);
        } else
            return CastUtil.cast(o);
    }

    public Class getByName(String name) throws ClassNotFoundException {
        Object o = Script.code(this,"[",name,"]");
        if (o == null || Script.isUndefined(o))
            throw new ClassNotFoundException(name);
        return get(o);
    }

    @CodeGenerator(ClassStorageGenerator.class)
    public static ClassStorage get() {return null;}
}
