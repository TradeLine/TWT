package org.tlsys.twt;

import org.tlsys.lex.Invoke;
import org.tlsys.lex.StaticRef;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.ArrayClass;
import org.tlsys.lex.declare.VClass;

public class DefaultCast implements ICastAdapter {
    @Override
    public Value cast(GenerationContext ctx, Value value, VClass to) throws CompileException {
        if (!(to instanceof ArrayClass) && to.alias.equals(String.class.getName())) {
            return new Invoke(value.getType().getMethod("toString"), value);
        } else {
            VClass classClass = value.getType().getClassLoader().loadClass(Class.class.getName());
            Invoke inv = new Invoke(classClass.getMethod("cast", value.getType().getClassLoader().loadClass(Object.class.getName())), new StaticRef(to));
            inv.arguments.add(value);
            inv.returnType = to;
            return inv;
            //throw new RuntimeException("Cast not supported");
        }
    }
}
