package org.tlsys;

import org.junit.Before;
import org.tlsys.RT.BooleanClass;
import org.tlsys.RT.ObjectClass;
import org.tlsys.RT.StringClass;
import org.tlsys.RT.TestClass;
import org.tlsys.twt.members.TClassLoader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class TestVM {
    public static VirtualFileProvider fs = new VirtualFileProvider();
    public static TClassLoader classLoader = new TClassLoader();
    public ObjectClass objectClass;
    public BooleanClass boolClass;
    public StringClass stringClass;
    public Generator g;
    public Output out;
    public ScriptEngine engine;

    @Before
    public void initVM() throws ScriptException {
        JavaSourceSet com = new JavaSourceSet(classLoader, fs);
        objectClass = TestClass.inject("org.tlsys", new ObjectClass(com.getClassLoader()), com);
        boolClass = TestClass.inject("org.tlsys", new BooleanClass(com.getClassLoader()), com);
        stringClass = TestClass.inject("org.tlsys", new StringClass(com.getClassLoader()), com);
        g = new Generator();
        out = new Output();
        ScriptEngineManager factory = new ScriptEngineManager();
        engine = factory.getEngineByName("JavaScript");
        engine.eval("Assert=org.junit.Assert");
    }
}
