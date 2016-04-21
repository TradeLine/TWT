package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.*;
import org.tlsys.twt.annotations.*;
import org.tlsys.twt.classes.ClassRecord;
import org.tlsys.twt.rt.EmptyMethodBody;

@JSClass
@ParentClass(value = "", implement = {})
@ClassName("java.lang.Object")
@ReplaceClass(java.lang.Object.class)
@CodeGenerator(DefaultGenerator.class)
@CastAdapter(DefaultCast.class)
public class TObject {

    public static final String CLASS_RECORD = "1:CLASS";
    private static int hashCodeCounter = 0;
    private int hashCode = ++hashCodeCounter;

    {
        Script.code("console.info('Object prototype is created! hashCode='+", CastUtil.toObject(hashCodeCounter), ")");
    }

    @CodeGenerator(EmptyMethodBody.class)
    //@MethodBodyGen("org.tlsys.twt.rt.EmptyMethodBody")
    public TObject() {
    }

    public static Class getClassOfObject(Object object) {
        if (Script.typeOf(object) == "number") {
            if (Script.code(object, "%1===0"))
                return int.class;
            else
                return float.class;
        }

        if (Script.typeOf(object) == "boolean")
            return boolean.class;

        if (Script.typeOf(object) == "string")
            return String.class;
        ClassRecord cr = Script.code(object, "[", CLASS_RECORD, "]");
        if (cr == null || Script.isUndefined(cr)) {
            Console.info("Error! Can't get class of object");
            Console.dir(object);
            Script.code("console.error(", object, ")");
            throw new RuntimeException("Can't get class of object");
        }
        return cr.getAsClass();
    }

    public static Integer fromint(int value) {
        return new Integer(value);
    }

    public static Long fromlong(long value) {
        return new Long(value);
    }

    public static Short fromshort(short value) {
        return new Short(value);
    }

    public static Byte frombyte(byte value) {
        return new Byte(value);
    }

    public static Float fromfloat(float value) {
        return new Float(value);
    }

    public static Double fromdouble(double value) {
        return new Double(value);
    }

    public static Object fromboolean(boolean value) {
        return new Boolean(true);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    //@InvokeGen("org.tlsys.twt.rt.java.lang.ObjectInvokeAdapter")
    @MethodName("getClass")
    @InvokeGen(ObjectInvokeAdapter.class)
    public Class getJClass() {
        throw new RuntimeException("Not supported");
    }

    //@InvokeGen(ApplyInvoke.class)
    public String toString() {
        /*
        if (this == null || Script.isUndefined(this))
            throw new NullPointerException();

        Class clazz = getClass();

        if (clazz == String.class)
            return CastUtil.cast(this);
*/
        return getClass().getName()+"@"+hashCode();
    }

    public boolean equals(Object obj) {
        return Script.code(CastUtil.toObject(this.hashCode()), "==", CastUtil.toObject(obj.hashCode()));
    }
}
