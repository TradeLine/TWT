package org.tlsys;

import org.junit.Test;

import javax.script.ScriptException;
import java.io.IOException;

public class ConstTest extends TestVM {

    @Test
    public void const_True() throws ScriptException, IOException {
        g.generate(new TestConst(true, boolClass, null), out);
        engine.eval("Assert.assertTrue(" + out.toString() + ")");
    }

    @Test
    public void const_False() throws ScriptException, IOException {
        g.generate(new TestConst(false, boolClass, null), out);
        engine.eval("Assert.assertFalse(" + out.toString() + ")");
    }

    @Test
    public void const_String() throws ScriptException, IOException {
        final String STR = "Hello from JAVA";
        g.generate(new TestConst(STR, stringClass, null), out);
        engine.eval("Assert.assertEquals(" + out.toString() + ", '" + STR + "')");
    }
}
