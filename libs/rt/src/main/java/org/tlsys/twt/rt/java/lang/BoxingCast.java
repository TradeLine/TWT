package org.tlsys.twt.rt.java.lang;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.VClass;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.GenerationContext;
import org.tlsys.twt.MainGenerationContext;
import org.tlsys.twt.ICastAdapter;

public class BoxingCast implements ICastAdapter {
    //@Override
    public String cast(MainGenerationContext context, Class from, Class to, JCTree.JCExpression value) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        /*
        Class fr = context.getClassAliaseProvider().getRealClass(from);
        Class tr = context.getClassAliaseProvider().getRealClass(to);
        Class fn = context.getClassAliaseProvider().getReplacedClass(from);
        Class tn = context.getClassAliaseProvider().getReplacedClass(to);

        if (fr == int.class && tr == Integer.class) {
            return context.getCodeBuilder().newInst(tn, tn.getConstructor(int.class), value);
        }

        if (Number.class.isAssignableFrom(fr) && tr == int.class) {
            return context.getCodeBuilder().invoke(value, tn.getMethod("intValue"));
        }

        if (Number.class.isAssignableFrom(fr) && tr == long.class) {
            return context.getCodeBuilder().invoke(value, tn.getMethod("longValue"));
        }

        if (Number.class.isAssignableFrom(fr) && tr == float.class) {
            return context.getCodeBuilder().invoke(value, tn.getMethod("floatValue"));
        }

        if (Number.class.isAssignableFrom(fr) && tr == double.class) {
            return context.getCodeBuilder().invoke(value, tn.getMethod("doubleValue"));
        }

        if (Number.class.isAssignableFrom(fr) && tr == byte.class) {
            return context.getCodeBuilder().invoke(value, tn.getMethod("byteValue"));
        }

        if (Number.class.isAssignableFrom(fr) && tr == short.class) {
            return context.getCodeBuilder().invoke(value, tn.getMethod("shortValue"));
        }



        if (fr == int.class && tr == Integer.class) {
            return context.getCodeBuilder().newInst(tn, tn.getConstructor(int.class), value);
        }

        if (fr == long.class && tr == Long.class) {
            return context.getCodeBuilder().newInst(tn, tn.getConstructor(long.class), value);
        }

        throw new RuntimeException("Not supported yet");
        */
        throw new RuntimeException("Not supported yet");
    }

    @Override
    public Value cast(GenerationContext ctx, Value value, VClass to) throws CompileException {
        throw new RuntimeException("not supported yet");
    }
}
