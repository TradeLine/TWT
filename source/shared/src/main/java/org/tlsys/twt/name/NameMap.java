package org.tlsys.twt.name;

import org.tlsys.lex.declare.VClass;

import java.io.*;
import java.util.HashMap;

public class NameMap implements Serializable {

    private static class ClassRecord implements Serializable {
        private transient VClass clazz;
        private final String realName;

        public ClassRecord(VClass clazz, String realName) {
            this.clazz = clazz;
            this.realName = realName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass())
                return false;

            ClassRecord that = (ClassRecord) o;

            return realName.equals(that.realName);
        }

        @Override
        public int hashCode() {
            return realName.hashCode();
        }
    }

    private final transient HashMap<VClass, ClassRecord> class_cash = new HashMap<>();
    private final HashMap<String, String> classs = new HashMap<>();

    private long classNameIterator;

    private ClassRecord getRecordFor(VClass clazz) {
        ClassRecord cr = class_cash.get(clazz);
        if (cr != null)
            return cr;
        String n = "c" + Long.toString(classNameIterator++, Character.MAX_RADIX).replace("-", "_");
        cr = new ClassRecord(clazz, n);
        classs.put(clazz.getRealName(), n);
        class_cash.put(clazz, cr);
        return cr;
    }

    public String getClassName(VClass clazz) {
        ClassRecord cr = getRecordFor(clazz);
        return cr.realName;
    }

    public void save(OutputStream stream) throws IOException {
        ObjectOutputStream s = new ObjectOutputStream(stream);
        s.writeObject(this);
    }

    public static NameMap load(InputStream stream) throws IOException, ClassNotFoundException {
        ObjectInputStream s = new ObjectInputStream(stream);
        return (NameMap) s.readObject();
    }
}
