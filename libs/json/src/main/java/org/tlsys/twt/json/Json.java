package org.tlsys.twt.json;

import org.tlsys.twt.Console;
import org.tlsys.twt.JDictionary;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.classes.ClassStorage;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@JSClass
public class Json {
    public static String toJSON(Object obj) {
        Console.info("try convert object to JSON");
        Console.dir(obj);
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
            Console.info("-1");

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
            Console.info("-2");

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
            Console.info("-3");

            String out = "{\"@type\":\"" + cl.getName() + "\"";
            Class clazz = cl;
            Console.info("-4");
            while (clazz != null) {
                Console.info("-4.1");
                for (Field f : clazz.getFields()) {
                    if (Modifier.isStatic(f.getModifiers()));
                    Console.info("-4.2 convert " + clazz.getName() + "::" + f.getName());
                    out += ",\"" + f.getName() + "\":" + toJSON(f.get(obj));
                }
                Console.info("-4.3");
                clazz = clazz.getSuperclass();
                Console.info("-4.4");
                if (clazz == Object.class)
                    break;
                Console.info("-4.5");
            }
            Console.info("-5");
            return out + "}";
        } catch (IllegalAccessException e) {
            Console.info("ERROR");
            throw new RuntimeException(e);
        }
    }

    public static Object fromJSON(String json) {
        Object ina = Script.code("JSON.parse(", json, ")");
        return readObject(ina, null, new JDictionary<>());
    }

    public static Object readObject(Object ina, Class needClass, JDictionary<Object> ids) {
        Console.info("READ=");
        Console.dir(ina);
        Console.info("LINE-1");
        try {
            Console.info("LINE-1-1");
            if (ina == null)
                return null;
            Console.info("LINE-1-2");
            String type = Script.typeOf(ina);
            if (type.equals("boolean") || type.equals("number") || type.equals("string"))
                return ina;
            Console.info("LINE-2");
            if (Script.code("Array.isArray(", ina, ")")) {
                Console.info("LINE-2-1");
                Console.info("Need class=");
                Console.dir(needClass);
                if (needClass.isArray()) {
                    Console.info("LINE-2-2");
                    int arLen = Script.code(ina, ".length");
                    Object ar = Array.newInstance(needClass.getComponentType(), arLen);
                    Script.code("console.info('2.3')");
                    for (int i = 0; i < arLen; i++) {
                        Script.code("console.info('2.4')");
                        Array.set(ar, i, readObject(Script.code(ina, "[", i, "]"), needClass.getComponentType(), ids));
                        Script.code("console.info('2.5')");
                    }
                    Script.code("console.info('2.6')");
                    return ar;
                }
                Script.code("console.info('2.7')");
                throw new RuntimeException("Not supported array type");
            }
            Console.info("LINE-3");
            if (Script.code(ina, ".hasOwnProperty('@ref')")) {
                return ids.get(Script.code(ina, "['@ref']"));
            }
            Console.info("LINE-4");
            String className = Script.code(ina, "['@type']");
            Console.info("LINE-5");
            if (needClass == null && (className == null || Script.isUndefined(className))) {
                throw new RuntimeException("Can't get class name...");
            }
            Console.info("LINE-6");
            Class objClass;
            if (className == null || Script.isUndefined(className)) {
                objClass = needClass;
            }else {
                objClass = ClassStorage.get().getByName(className);
            }
            Console.info("LINE-7");

            if (objClass == boolean.class)
                return Script.code(ina, ".value");
            if (objClass.isArray()) {
                return readObject(Script.code(ina, "['@items']"), objClass, ids);
            }
            Console.info("LINE-8=>" + objClass.getName());
            Object o = objClass.newInstance();
            Console.info("LINE-9");
            if (Script.code(ina, ".hasOwnProperty('@id')")) {
                int id = Script.code(ina, "['@id']");
                ids.set(id, o);
            }

            Class oo = objClass;
            Console.info("LINE-10");
            while (oo != null) {
                Field[] fields = oo.getFields();
                for (Field f : fields) {
                    Console.info("Read field " + f.getName() + " with type " + f.getType());
                    if (Script.code(ina, ".hasOwnProperty(", f.getName(), ")")) {
                        Object t = Script.code(ina, "[", f.getName(), "]");
                        f.set(o, readObject(t, f.getType(), ids));
                    }
                }
                oo = oo.getSuperclass();
                if (oo == Object.class)
                    break;
            }
            Console.info("LINE-11");
            return o;
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            Console.info("ERROR2");
            throw new RuntimeException(e);
        }
    }
}
