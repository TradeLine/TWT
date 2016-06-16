package org;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.junit.Test;
import org.tlsys.Compile;
import org.tlsys.Pass1;
import org.tlsys.ast.TypeDeclaration;
import org.tlsys.parser.Parser;
import org.tlsys.utils.ClassUnit;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

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
