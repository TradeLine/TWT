package org.tlsys.twt;

import org.tlsys.twt.desc.ClassDesc;
import org.tlsys.twt.desc.FieldDesc;
import org.tlsys.twt.rt.java.lang.reflect.TField;

import java.io.PrintStream;
import java.lang.reflect.Field;

public class FullMeta implements IMetaFactory {
    @Override
    public void genMeta(GenContext ctx, ClassDesc desc, PrintStream ps) throws NoSuchMethodException, ClassNotFoundException {
        ps.append("function(){");
        for (FieldDesc fd : desc.getFields()) {
            ps.append("this.addField(");
            String s = ctx.getCodeBuilder().newInst(Field.class, TField.class.getConstructor(String.class, String.class, Class.class, Class.class, boolean.class),
                    "'"+fd.getName() + "'",
                    "'"+fd.getJsName()+"'",
                    ctx.getCodeBuilder().getClass(ctx.getClass(fd.getType())),
                    ctx.getCodeBuilder().getClass(ctx.getClass(desc.getName())),
                    fd.isStatic()?"true":"false"
            );
            ps.append(s);
            //System.out.println("->" + s + " " + desc);
            ps.append(");");
        }
        ps.append("\n}");
    }
}
