package org.tlsys.twt;

import org.tlsys.CodeBuilder;
import org.tlsys.TypeUtil;
import org.tlsys.lex.ClassRef;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.ArrayClass;
import org.tlsys.lex.declare.VClass;
import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.rt.boxcastadapter.BoxCastAdapter;

public class DefaultCast implements ICastAdapter {
    @Override
    public Value cast(Value value, VClass to, SourcePoint point) throws CompileException {

        if (TypeUtil.isPrimitive(value.getType()))
            return new BoxCastAdapter().cast(value, to, point);


        if (!(to instanceof ArrayClass) && to.alias != null && to.alias.equals(String.class.getName())) {
            Value val = CodeBuilder.scopeStatic(to).method("valueOf").arg(value.getType()).invoke().arg(value).build();
            return val;
            //return new Invoke(value.getType().getMethod("toString"), value);
        } else {
            return CodeBuilder
                    .scope(new ClassRef(to, null))
                    .method("cast")
                    .arg(value.getType().getClassLoader().loadClass(Object.class.getName(), point))
                    .invoke()
                    .arg(value)
                    .build();
            /*
            VClass classClass = value.getType().getClassLoader().loadClass(Class.class.getName());

            Invoke inv = new Invoke(classClass.getMethod("cast", value.getType().getClassLoader().loadClass(Object.class.getName())), new StaticRef(to));
            inv.arguments.add(value);
            inv.returnType = to;
            return inv;
            */
        }
    }
}
