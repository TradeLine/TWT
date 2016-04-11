package org.tlsys;

import org.junit.Test;
import org.tlsys.lex.declare.VClassLoader;
import org.tlsys.lex.members.VClass;

import java.util.Optional;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class JavaCompillerTest {

    @Test
    public void test() {
        StringBuilder sb =new StringBuilder();

        sb.append("package org.tlsys;").
                append("public class Main {")
                .append("public static void main(){}")
                .append("}");

        VirtualFileProvider fs = new VirtualFileProvider();
        fs.getRoot().dir("org").dir("tlsys").file("Main.java", sb.toString().getBytes());


        VClassLoader cl = new VClassLoader();
        JavaSourceSet com = new JavaSourceSet(cl, fs);

        Optional<VClass> op = com.getClass("org.tlsys.Main");

        assertNotNull(op);
        assertTrue(op.isPresent());
    }
}
