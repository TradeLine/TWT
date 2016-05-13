package org.tlsys.twt;

import org.tlsys.twt.links.ClassVal;
import org.tlsys.twt.links.MethodVal;

import java.util.List;

public abstract class ExecuteRecolver {
    private static ExecuteRecolver def;

    public static ExecuteRecolver get() {
        return def;
    }

    public static void set(ExecuteRecolver def) {
        ExecuteRecolver.def = def;
    }

    public static MethodVal reloveLocal(ClassVal parent, String name, List<ClassVal> arguments) {
        return def.resolveLocalMethod(parent, name, arguments);
    }

    public static MethodVal reloveStatic(ClassVal parent, String name, List<ClassVal> arguments) {
        return def.reloveStaticMethod(parent, name, arguments);
    }

    public static MethodVal reloveConstructor(ClassVal parent, List<ClassVal> arguments) {
        return def.resolveLocalConstructor(parent, arguments);
    }

    protected abstract MethodVal resolveLocalMethod(ClassVal parent, String name, List<ClassVal> arguments);

    protected abstract MethodVal reloveStaticMethod(ClassVal parent, String name, List<ClassVal> arguments);

    protected abstract MethodVal resolveLocalConstructor(ClassVal parent, List<ClassVal> arguments);

}
