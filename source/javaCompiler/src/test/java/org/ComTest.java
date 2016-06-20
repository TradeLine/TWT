package org;

import org.junit.Test;
import org.tlsys.TWTCompiler;
import org.tlsys.compiler.Compile;
import org.tlsys.compiler.ast.TypeDeclaration;
import org.tlsys.compiler.parser.Parser;
import org.tlsys.compiler.utils.ClassUnit;

import java.io.IOException;

/**
 * Created by Субочев Антон on 16.06.2016.
 */
public class ComTest {
    @Test
    public void test() throws IOException {
        TWTCompiler.compile(ComTest.class.getClassLoader().getResourceAsStream(TestClass.class.getName().replace('.', '/')+".class"));
    }

    public static void tempMethod() {
        //
    }

    public static void getLambda(MyLambda value) {

    }
}
