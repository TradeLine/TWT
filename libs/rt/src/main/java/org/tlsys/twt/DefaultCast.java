package org.tlsys.twt;

import org.tlsys.CodeBuilder;
import org.tlsys.lex.ClassRef;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.ArrayClass;
import org.tlsys.lex.declare.VClass;
import org.tlsys.sourcemap.SourcePoint;

public class DefaultCast implements ICastAdapter {
    @Override
    public Value cast(Value value, VClass to, SourcePoint point) throws CompileException {
        if (!(to instanceof ArrayClass) && to.alias.equals(String.class.getName())) {
            Value val = CodeBuilder.scopeStatic(to).method("valueOf").arg(value.getType()).invoke(point).arg(value).build();
            return val;
            //return new Invoke(value.getType().getMethod("toString"), value);

        } else {
            return CodeBuilder
                    .scope(new ClassRef(to, null))
                    .method("cast")
                    .arg(value.getType().getClassLoader().loadClass(Object.class.getName(), point))
                    .invoke(point)
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
