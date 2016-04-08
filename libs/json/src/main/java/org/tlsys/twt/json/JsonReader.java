package org.tlsys.twt.json;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.classes.ClassRecord;
import org.tlsys.twt.classes.ClassStorage;
import org.tlsys.twt.rt.java.lang.TClass;
import org.tlsys.twt.rt.java.lang.TObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
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
            if (TObject.getClassOfObject(o) == int.class) {
                return new Integer(CastUtil.toInt(o));
            }
        }

        if (type == "boolean") {
            if (TObject.getClassOfObject(o) == int.class) {
                return new Boolean(CastUtil.toBoolean(o));
            }
        }

        if (type == "object") {
            if (CastUtil.toBoolean(Script.code("Array.isArray(", o, ")"))) {
                int len = CastUtil.toInt(Script.code(o, ".length"));
                if (needClass == null) {
                    TClass cl = CastUtil.cast(Object.class);
                    needClass = cl.getRecord().getArrayClassRecord().getAsClass();
                }

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
                return readObject(o);
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

    private static Object readObject(Object o) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (o == null)
            return null;
        String type = Script.code(o, "['@type']");

        ClassRecord cr = ClassStorage.get().getByName(type);
        Class cl = cr.getAsClass();

        Object items = Script.code(o, "['@items']");
        if (items != null && !Script.isUndefined(items)) {
            return read(items, cl);
        }

        Object v = cl.newInstance();

        while (cl != null) {
            if (cl == Object.class)
                break;

            Field[] fields = cl.getFields();
            for (Field f : fields) {
                if (!Script.hasOwnProperty(o, f.getName()))//if the value of the field name is not found
                    continue;//do not work ok
                f.set(v, read(Script.code(o, "[", f.getName(), "]"), f.getType()));
            }
            cl = cl.getSuperclass();
        }
        return v;
    }
}
