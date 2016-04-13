package org.tlsys.twt.rt.boxcastadapter;

import org.tlsys.CodeBuilder;
import org.tlsys.TypeUtil;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.VClass;
import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.ICastAdapter;

public class BoxCastAdapter implements ICastAdapter {

    protected Class getPrimitiveType(){
        throw new RuntimeException("!!!");
    }
    protected Class getObjectType() {
        throw new RuntimeException("!!!");
    }

    @Override
    public Value cast(Value from, VClass to, SourcePoint p) throws CompileException {
        if (TypeUtil.isPrimitive(to)) {
            if (TypeUtil.isPrimitive(from.getType()))
                return CodeBuilder.scopeStatic(from.getType()).method(to.alias + "Value").arg(from.getType()).invoke().arg(from).build();
            else
                return CodeBuilder.scope(from).method(to.alias + "Value").invoke().build();
        } else {
            if (TypeUtil.isPrimitive(from.getType()))
                return CodeBuilder.scopeStatic(to).method("from" + from.getType().alias.replace(".", "_")).arg(from.getType()).invoke().arg(from).build();
            else
                return CodeBuilder.scopeStatic(to).method("from" + from.getType().alias.replace(".", "_")).arg(from.getType()).invoke().arg(from).build();
                //return CodeBuilder.constructor(to).arg(from.getType()).invoke(p).arg(from).build();
        }

        //throw new RuntimeException("Can't cast " + from.getType().fullName + " to " + to.fullName);
        /*

        if (to.isThis(getPrimitiveType().getName()))
            return toPrimitive(from, to, sourcePoint);

        if (to.isThis(getObjectType().getName()))
            return toObject(from, to, sourcePoint);



        */
    }
/*
    protected Value toObject(Value from, VClass to, SourcePoint p) {
        return CodeBuilder.constructor(from.getType()).arg(to).invoke(p).arg(from).build();
    }

    protected Value toPrimitive(Value from, VClass to, SourcePoint p) {
        return CodeBuilder.scope(from).method(getPrimitiveType().getSimpleName()+"Value").arg(to).invoke(p).arg(from).build();
    }
    */
}
