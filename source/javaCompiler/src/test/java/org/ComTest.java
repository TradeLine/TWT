package org;

import org.junit.Test;
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
        ClassUnit cu = Compile.getInstance().getOrCreateClassUnit(TestClass.class.getName());
        /*
        InputStream classStream = ComTest.class.getClassLoader().getResourceAsStream(TestClass.class.getName().replace('.','/')+".class");
        System.out.println(""+classStream);
        ClassParser cp = new ClassParser(classStream, TestClass.class.getSimpleName());
        JavaClass javaClass=cp.parse();
        */
        long t = System.currentTimeMillis();
        long time = System.nanoTime();
        Parser p = new Parser(cu);
        TypeDeclaration td = p.parse();
        time = System.nanoTime() - time;
        float sec = time / 1000.f;
        sec = sec / 1000.f;
        System.out.println(sec);
        System.out.println(System.currentTimeMillis()-t);

        Compile c = Compile.getInstance();

        td.visit(new MyVisiter());

        System.out.println(p+""+c +""+td);
    }

    public static void tempMethod() {
        //
    }

    public static void getLambda(MyLambda value) {

    }
}
