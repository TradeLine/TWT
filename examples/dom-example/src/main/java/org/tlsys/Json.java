package org.tlsys;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

@JSClass
public class Json {
    public static String toJSON(Object obj) {
        try {
            if (obj == null)
                return "null";
            if (/*Script.typeOf(obj) == "string" || */obj instanceof String) {
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

            String out = "{\"@type\":\"" + cl.getName() + "\"";
            Class clazz = cl;
            while (clazz != null) {
                for (Field f : clazz.getFields()) {
                    out += ",\"" + f.getName() + "\":" + toJSON(f.get(obj));
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
}
