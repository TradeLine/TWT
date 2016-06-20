package org.tlsys.twt.nodes;

/**
 * Created by Субочев Антон on 20.06.2016.
 */
public class TPrimitive extends SimpleTClass {
    public TPrimitive(String name) {
        super(name, new TMethod[0], new SimpleClassReferance(Object.class.getName().replace('.', '/')), new SimpleClassReferance[0]);
    }

    public static final TClass BOOLEAN = new TPrimitive("boolean");
    public static final TClass BYTE = new TPrimitive("byte");
    public static final TClass CHAR = new TPrimitive("char");
    public static final TClass SHORT = new TPrimitive("short");
    public static final TClass INT = new TPrimitive("int");
    public static final TClass LONG = new TPrimitive("long");
    public static final TClass FLOAT = new TPrimitive("float");
    public static final TClass DOUBLE = new TPrimitive("double");
}
