package org.tlsys.twt.json;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.JDictionary;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.classes.ClassStorage;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

@JSClass
public class Json {
    public static String toJSON(Object obj) {
        if (Script.isUndefined(obj))
            return null;
        try {
            if (Script.typeOf(obj)=="number")
                return ""+obj;
            if (obj == null)
                return "null";
            if (obj instanceof String) {
                return "\"" + obj + "\"";
            }

            Class cl = obj.getClass();
            if (cl.isPrimitive()) {
                return obj.toString();
            }

            if (cl.isArray()) {
                String out = "[";
                boolean first = true;
                int len = Array.getLength(obj);
                for (int i = 0; i < len; i++) {
                    if (!first)
                        out += ",";
                    out += toJSON(Array.get(obj, i));
                    first = false;
                }
                out += "]";
                return out;
            }

            if (Iterable.class.isAssignableFrom(cl)) {
                Iterable im = (Iterable) obj;
                String out = "[";
                boolean first = true;
                for (Object o : im) {
                    if (!first)
                        out += ",";
                    out += toJSON(o);
                    first = false;
                }
                out += "]";
                return out;
            }

            if (obj.getClass() == Date.class) {
                Date data = (Date)obj;
                return data.getTime()+"";
            }


            String out = "{\"@type\":\"" + cl.getName() + "\"";
            Class clazz = cl;
            while (clazz != null) {
                for (Field f : clazz.getFields()) {
                    if (!Modifier.isStatic(f.getModifiers())) {
                        out += ",\"" + f.getName() + "\":" + toJSON(f.get(obj));
                    }
                }
                clazz = clazz.getSuperclass();
                if (clazz == Object.class)
                    break;
            }
            return out + "}";
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object fromJSON(String json) {
        Object ina = Script.code("JSON.parse(", json, ")");
        return readObject(ina, null, new JDictionary<>());
    }

    public static Object readObject(Object ina, Class needClass, JDictionary<Object> ids) {
        try {
            if (ina == null)
                return null;
            String type = Script.typeOf(ina);
            if (type.equals("boolean") || type.equals("number") || type.equals("string"))
                return ina;
            if (Script.code("Array.isArray(", ina, ")")) {
                if (needClass.isArray()) {
                    int arLen = CastUtil.toInt(Script.code(ina, ".length"));
                    Object ar = Array.newInstance(needClass.getComponentType(), arLen);
                    for (int i = 0; i < arLen; i++) {
                        Array.set(ar, i, readObject(Script.code(ina, "[", i, "]"), needClass.getComponentType(), ids));
                    }
                    return ar;
                }
                throw new RuntimeException("Not supported array type");
            }
            if (CastUtil.toBoolean(Script.code(ina, ".hasOwnProperty('@ref')"))) {
                return ids.get(Script.code(ina, "['@ref']"));
            }
            String className = Script.code(ina, "['@type']");
            if (needClass == null && (className == null || Script.isUndefined(className))) {
                throw new RuntimeException("Can't get class name...");
            }
            if (needClass == Date.class) {
                long time = CastUtil.cast(ina);
                return new Date(time);
            }
            Class objClass;
            if (className == null || Script.isUndefined(className)) {
                objClass = needClass;
            }else {
                objClass = ClassStorage.get().getByName(className);
            }

            if (objClass == boolean.class)
                return Script.code(ina, ".value");
            if (objClass.isArray()) {
                return readObject(Script.code(ina, "['@items']"), objClass, ids);
            }
            Object o = objClass.newInstance();
            if (CastUtil.toBoolean(Script.code(ina, ".hasOwnProperty('@id')")) ){
                int id = CastUtil.toInt(Script.code(ina, "['@id']"));
                ids.set(id, o);
            }

            Class oo = objClass;
            while (oo != null) {
                Field[] fields = oo.getFields();
                for (Field f : fields) {
                    if (CastUtil.toBoolean(Script.code(ina, ".hasOwnProperty(", f.getName(), ")"))) {
                        Object t = Script.code(ina, "[", f.getName(), "]");
                        f.set(o, readObject(t, f.getType(), ids));
                    }
                }
                oo = oo.getSuperclass();
                if (oo == Object.class)
                    break;
            }
            return o;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
