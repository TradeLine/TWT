package org.tlsys.twt;

import org.junit.Test;
import org.tlsys.CompileResult;
import org.tlsys.TWTCompiler;

import java.io.IOException;
import java.io.InputStream;

public class TestGenerate {
    @Test
    public void test() throws IOException {
        InputStream is = TestGenerate.class.getClassLoader().getResourceAsStream(TestClass.class.getName().replace('.','/')+".class");

        System.out.println(is);
        CompileResult cr = TWTCompiler.compile(is);

        NativeClassCodeGenerator generator = new NativeClassCodeGenerator();
        generator.generate(cr.getResult(), System.out, null);
    }
}
