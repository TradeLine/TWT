package org.tlsys.twt.json;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Console;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.classes.ClassRecord;
import org.tlsys.twt.classes.ClassStorage;
import org.tlsys.twt.rt.java.lang.TClass;
import org.tlsys.twt.rt.java.lang.TObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

@JSClass
public final class JsonReader {
    private JsonReader() {
    }

    private static Object read(Object o, Class needClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String type = Script.typeOf(o);

        if (type == "string") {
            return o;
        }

        if (type == "number") {

            if (TObject.getClassOfObject(o) == float.class) {
                if (needClass == byte.class)
                    return new Byte(CastUtil.toByte(o));

                if (needClass == double.class)
                    return new Double(CastUtil.toDouble(o));

                return new Float(CastUtil.toFloat(o));
            }

            if (TObject.getClassOfObject(o) == double.class) {
                if (needClass == float.class)
                    return new Float(CastUtil.toFloat(o));
                return new Double(CastUtil.toDouble(o));
            }

            if (TObject.getClassOfObject(o) == byte.class) {
                return new Byte(CastUtil.toByte(o));
            }

            if (TObject.getClassOfObject(o) == short.class) {
                return new Short(CastUtil.toShort(o));
            }

            if (TObject.getClassOfObject(o) == int.class) {
                if (needClass == float.class)
                    return new Float(CastUtil.toFloat(o));
                if (needClass == double.class)
                    return new Double(CastUtil.toDouble(o));
                return new Integer(CastUtil.toInt(o));
            }

            if (TObject.getClassOfObject(o) == long.class) {
                return new Long(CastUtil.toLong(o));
            }
        }

        if (type == "boolean") {
            if (TObject.getClassOfObject(o) == boolean.class) {
                return new Boolean(CastUtil.toBoolean(o));
            }
        }

        if (type == "object") {

            if (needClass == null) {
                TClass cl = CastUtil.cast(Object.class);
                needClass = cl.getRecord().getArrayClassRecord().getAsClass();
            }

            Console.info("read object...");
            if (CastUtil.toBoolean(Script.code("Array.isArray(", o, ")"))) {
                Console.info("Object is ARRAY...");
                int len = CastUtil.toInt(Script.code(o, ".length"));

                needClass = needClass.getComponentType();

                Object ar = CastUtil.cast(Array.newInstance(needClass, len));
                for (int i = 0; i < len; i++) {
                    Objects item = Script.code(o, "[", CastUtil.toObject(i), "]");
                    /*
                    if (needClass.isPrimitive())
                        Array.set(ar, i, BoxCast.toPrimitive(needClass, read(item, needClass)));
                    else
                    */
                    Array.set(ar, i, read(item, needClass));
                }

                return ar;
            } else {
                Console.info("Object is OBJECT...");
                return readObject(o, needClass);
            }
        }

        throw new RuntimeException("Unknown type");
    }

    public static Object jsonToObject(String json) {

        try {
            Object ina = Script.code("JSON.parse(", json, ")");
            return read(ina, null);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object readObject(Object o, Class needClass) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (o == null)
            return null;
        String type = Script.code(o, "['@type']");

        Class cl = null;
        if (type == null || Script.isUndefined(type)) {
            cl = needClass;
        } else {
            Console.info("Search class " + type + "...");

            ClassRecord cr = ClassStorage.get().getByName(type);
            cl = cr.getAsClass();
        }

        if (!Script.isUndefined(Script.code(o, ".value"))) {
            if (cl == boolean.class)
                return new Boolean(CastUtil.toBoolean(Script.code(o, ".value")));

            if (cl == char.class)
                return new Character(CastUtil.toChar(Script.code(o, ".value")));

            if (cl == byte.class)
                return new Byte(CastUtil.toByte(Script.code(o, ".value")));

            if (cl == short.class)
                return new Short(CastUtil.toShort(Script.code(o, ".value")));

            if (cl == int.class)
                return new Integer(CastUtil.toInt(Script.code(o, ".value")));

            if (cl == long.class)
                return new Long(CastUtil.toLong(Script.code(o, ".value")));

            if (cl == float.class)
                return new Float(CastUtil.toFloat(Script.code(o, ".value")));

            if (cl == double.class)
                return new Double(CastUtil.toDouble(Script.code(o, ".value")));
        }


        Object items = Script.code(o, "['@items']");
        if (items != null && !Script.isUndefined(items)) {
            return read(items, cl);
        }

        Object v = cl.newInstance();
        Console.info("Set fields...");
        while (cl != null) {
            if (cl == Object.class)
                break;

            Field[] fields = cl.getFields();
            for (Field f : fields) {
                if (Modifier.isTransient(f.getModifiers()))
                    continue;
                if (Modifier.isStatic(f.getModifiers()))
                    continue;
                if (!Script.hasOwnProperty(o, f.getName()))//if the value of the field name is not found
                    continue;//do not work ok
                Console.info("read value for " + f.getName() + "...");
                Object o2 = read(Script.code(o, "[", f.getName(), "]"), f.getType());
                Console.dir(o2);
                f.set(v, o2);
            }
            cl = cl.getSuperclass();
        }
        return v;
    }
}
