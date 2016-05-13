package org.tlsys.twt;

import org.tlsys.twt.links.ClassVal;
import org.tlsys.twt.members.VClass;

public abstract class ClassResolver {
    private static ClassResolver def;

    public static ClassResolver get() {
        return def;
    }

    public static VClass resolve(String name) throws TClassNotFoundException {
        return get().resolveClass(name);
    }

    public static VClass resolve(NamedClassVal ref) throws TClassNotFoundException {
        return resolve(ref.getClassName());
    }

    public static VClass resolve(ClassVal ref) throws TClassNotFoundException {
        return get().resolveClass(ref);
    }

    public static VClass resolve(Class clazz) throws TClassNotFoundException {
        return resolve(clazz.getName());
    }

    public void setDefault(ClassResolver def) {
        this.def = def;
    }

    public abstract VClass resolveClass(String name) throws TClassNotFoundException;

    public abstract VClass resolveClass(ClassVal name) throws TClassNotFoundException;
}
