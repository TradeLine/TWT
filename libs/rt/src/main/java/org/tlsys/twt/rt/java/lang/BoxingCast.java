package org.tlsys.twt.rt.java.lang;

import org.tlsys.lex.Invoke;
import org.tlsys.lex.NewClass;
import org.tlsys.lex.StaticRef;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.VClass;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.GenerationContext;
import org.tlsys.twt.MainGenerationContext;
import org.tlsys.twt.ICastAdapter;

public class BoxingCast implements ICastAdapter {
    /*
    //@Override
    public String cast(MainGenerationContext context, Class from, Class to, JCTree.JCExpression value) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {

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

        throw new RuntimeException("Not supported yet");
    }
    */


    @Override
    public Value cast(Value value, VClass vClass) throws CompileException {

        if (char.class.getName().equals(value.getType().alias)) {
            if (Character.class.getName().equals(vClass.alias)) {
                return new NewClass(vClass.getConstructor(value.getType())).addArg(value);
            }
        }

        if (byte.class.getName().equals(value.getType().alias)) {
            if (Byte.class.getName().equals(vClass.alias)) {
                return new NewClass(vClass.getConstructor(value.getType())).addArg(value);
            }
        }

        if (short.class.getName().equals(value.getType().alias)) {
            if (Short.class.getName().equals(vClass.alias)) {
                return new NewClass(vClass.getConstructor(value.getType())).addArg(value);
            }

            if (byte.class.getName().equals(vClass.alias)) {
                return new Invoke(vClass.getMethod("fromShort", value.getType()), new StaticRef(vClass)).addArg(value);
            }

            if (int.class.getName().equals(vClass.alias)) {
                return value;
            }

            if (long.class.getName().equals(vClass.alias)) {
                return value;
            }
        }

        if (int.class.getName().equals(value.getType().alias)) {
            if (Integer.class.getName().equals(vClass.alias)) {
                return new NewClass(vClass.getConstructor(value.getType())).addArg(value);
            }

            if (byte.class.getName().equals(vClass.alias)) {
                return new Invoke(vClass.getMethod("fromInt", value.getType()), new StaticRef(vClass)).addArg(value);
            }

            if (long.class.getName().equals(vClass.alias)) {
                return value;
            }

            if (float.class.getName().equals(vClass.alias)) {
                return value;
            }

            if (double.class.getName().equals(vClass.alias)) {
                return value;
            }
        }

        if (long.class.getName().equals(value.getType().alias)) {
            if (Long.class.getName().equals(vClass.alias)) {
                return new NewClass(vClass.getConstructor(value.getType())).addArg(value);
            }

            if (byte.class.getName().equals(vClass.alias)) {
                return new Invoke(vClass.getMethod("fromLong", value.getType()), new StaticRef(vClass)).addArg(value);
            }

        }

        if (float.class.getName().equals(value.getType().alias)) {
            if (Float.class.getName().equals(vClass.alias)) {
                return new NewClass(vClass.getConstructor(value.getType())).addArg(value);
            }
        }

        if (double.class.getName().equals(value.getType().alias)) {
            if (Double.class.getName().equals(vClass.alias)) {
                return new NewClass(vClass.getConstructor(value.getType())).addArg(value);
            }
        }


        VClass numberClass = vClass.getClassLoader().loadClass(Number.class.getName());

        if (value.getType().isParent(numberClass)) {
            if (int.class.getName().equals(vClass.alias)) {
                return new Invoke(value.getType().getMethod("intValue"), value);
            }

            if (long.class.getName().equals(vClass.alias)) {
                return new Invoke(value.getType().getMethod("longValue"), value);
            }

            if (float.class.getName().equals(vClass.alias)) {
                return new Invoke(value.getType().getMethod("floatValue"), value);
            }

            if (double.class.getName().equals(vClass.alias)) {
                return new Invoke(value.getType().getMethod("doubleValue"), value);
            }

            if (byte.class.getName().equals(vClass.alias)) {
                return new Invoke(value.getType().getMethod("byteValue"), value);
            }

            if (short.class.getName().equals(vClass.alias)) {
                return new Invoke(value.getType().getMethod("shortValue"), value);
            }
        }
        throw new RuntimeException("Can't cast " + value.getType().getRealName() + " to " + vClass.getRealName());
    }
}
