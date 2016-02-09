package org.tlsys.twt.classes;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.JArray;
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
        return cr;
    }

    public Class get(Object o) {
        if (Script.isPrototypeOf(o, ClassRecord.class)) {
            ClassRecord cr = CastUtil.cast(o);
            TClass clazz = new TClass(cr.getJsName());
            Script.code(this, "[", cr.getJsName(),"]=",clazz);
            clazz.initFor(cr);
            return CastUtil.cast(clazz);
        } else
            return CastUtil.cast(o);
    }
}
