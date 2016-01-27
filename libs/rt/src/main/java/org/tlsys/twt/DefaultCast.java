package org.tlsys.twt;

import org.tlsys.lex.Invoke;
import org.tlsys.lex.StaticRef;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.VClass;

/**
 * Created by Субочев Антон on 27.01.2016.
 */
public class DefaultCast implements ICastAdapter {
    @Override
    public Value cast(GenerationContext ctx, Value value, VClass to) throws CompileException {
        if (to.alias.equals(String.class.getName())) {
            return new Invoke(value.getType().getMethod("toString"), value);
        } else {
            VClass classClass = value.getType().getClassLoader().loadClass(Class.class.getName());
            Invoke inv = new Invoke(classClass.getMethod("cast", value.getType().getClassLoader().loadClass(Object.class.getName())), new StaticRef(to));
            inv.arguments.add(value);
            return inv;
            //throw new RuntimeException("Cast not supported");
        }
    }
}
