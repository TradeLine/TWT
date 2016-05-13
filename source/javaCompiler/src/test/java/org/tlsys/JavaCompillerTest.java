package org.tlsys;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import org.tlsys.RT.IntClass;
import org.tlsys.RT.ObjectClass;
import org.tlsys.RT.TestClass;
import org.tlsys.java.lex.JavaBlock;
import org.tlsys.twt.expressions.TAssign;
import org.tlsys.twt.expressions.TVarDeclare;
import org.tlsys.twt.members.MehtodSearchRequest;
import org.tlsys.twt.members.TField;
import org.tlsys.twt.members.VClass;
import org.tlsys.twt.members.VMethod;
import org.tlsys.twt.statement.StaExpression;

import java.util.Optional;

import static junit.framework.Assert.*;

public class JavaCompillerTest {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(JavaCompillerTest.class.getName());

    private String addSimpleClass(VirtualFileProvider fs, String name, String body) {

        StringBuilder sb = new StringBuilder();
        sb.append("package org.tlsys;").
                append("public class " + name + " {");
        if (body != null)
            sb.append(body);
        sb.append("}");
        fs.getRoot().dir("org").dir("tlsys").file(name + ".java", sb.toString().getBytes());

        return "org.tlsys." + name;
    }

    private String addSimpleClass(VirtualFileProvider fs, String name) {
        return addSimpleClass(fs, name, null);
    }

    private void addSimpleNativeClass(VirtualFileProvider fs, String name, CompileClassLoader loader) {
        loader.addAlias(name, addSimpleClass(fs, "T" + name));
    }

    //@Test
    public void test() {
        StringBuilder sb = new StringBuilder();

        sb.append("package org.tlsys;").
                append("public class Main {")
                .append("public static void main(){int a = 8;}")
                .append("}");

        VirtualFileProvider fs = new VirtualFileProvider();
        fs.getRoot().dir("org").dir("tlsys").file("Main.java", sb.toString().getBytes());

        CompileClassLoader classLoader = new CompileClassLoader();

        addSimpleNativeClass(fs, "void", classLoader);


        JavaSourceSet com = new JavaSourceSet(classLoader, fs);
        classLoader.setJavaSourceSet(com);

        Optional<VClass> op = classLoader.findClassByName("org.tlsys.Main");

        assertNotNull(op);
        assertTrue(op.isPresent());

        Optional<VMethod> method = op.get().findMethod("main", MehtodSearchRequest.of(null));
        //assertTrue(method.isPresent());

        //assertEquals(method.get().getResult(), classLoader.findClassByName("void").get());
        //assertEquals(method.get().getArguments().size(), 0);
    }

    //@Test
    public void testParseVarDeclaration() throws ParseException {
        VirtualFileProvider fs = new VirtualFileProvider();

        //addSimpleNativeClass(fs, "int");

        CompileClassLoader classLoader = new CompileClassLoader();

        JavaSourceSet com = new JavaSourceSet(classLoader, fs);
        classLoader.setJavaSourceSet(com);
        TestClass.inject("org.tlsys", new ObjectClass(com.getClassLoader()), com);
        TestClass.inject("org.tlsys", new IntClass(com.getClassLoader()), com);

        VClass clazz = classLoader.findClassByName(addSimpleClass(fs, "Test")).get();

        JavaBlock block = JavaCompiller.statement(JavaParser.parseBlock("{int a=8;}"), clazz);
        StaExpression exp = (StaExpression) block.getStatement(0);
        TVarDeclare jvd = (TVarDeclare) exp.getExpression();
        assertEquals(jvd.getVars().length, 1);
        assertEquals(jvd.getVars()[0].getType(), classLoader.findClassByName("int").get());
    }

    //@Test
    public void testParseVarSet() throws ParseException {
        VirtualFileProvider fs = new VirtualFileProvider();
        CompileClassLoader classLoader = new CompileClassLoader();
        addSimpleNativeClass(fs, "int", classLoader);



        JavaSourceSet com = new JavaSourceSet(classLoader, fs);
        classLoader.setJavaSourceSet(com);

        VClass clazz = classLoader.findClassByName(addSimpleClass(fs, "Test")).get();

        JavaBlock block = JavaCompiller.statement(JavaParser.parseBlock("{int a = 10; a = 8;}"), clazz);

        StaExpression ex = (StaExpression) block.getStatement(1);
        assertTrue(ex.getExpression() instanceof TAssign);
    }

    //@Test
    public void fieldDeclare() throws ParseException {
        StringBuilder sb = new StringBuilder();

        sb.append("package org.tlsys;").
                append("public class Main {")
                .append("public int a;")
                .append("public static void main(){int a = 8;}")
                .append("}");

        VirtualFileProvider fs = new VirtualFileProvider();
        CompileClassLoader classLoader = new CompileClassLoader();
        addSimpleNativeClass(fs, "int", classLoader);
        fs.getRoot().dir("org").dir("tlsys").file("Main.java", sb.toString().getBytes());



        JavaSourceSet com = new JavaSourceSet(classLoader, fs);
        classLoader.setJavaSourceSet(com);

        VClass clazz = classLoader.findClassByName("org.tlsys.Main").get();
        Optional<TField> v = clazz.getField("a");
        assertTrue(v.isPresent());
    }

    //@Test
    public void findField() throws ParseException {
        StringBuilder sb = new StringBuilder();

        sb.append("package org.tlsys;").
                append("public class Main {")
                .append("public int a;")
                .append("public Main b;")
                .append("public org.tlsys.Main c;")
                .append("public static void main(){int a = 8;}")
                .append("}");

        VirtualFileProvider fs = new VirtualFileProvider();
        CompileClassLoader classLoader = new CompileClassLoader();
        addSimpleNativeClass(fs, "int", classLoader);
        fs.getRoot().dir("org").dir("tlsys").file("Main.java", sb.toString().getBytes());


        JavaSourceSet com = new JavaSourceSet(classLoader, fs);
        classLoader.setJavaSourceSet(com);

        VClass clazz = classLoader.findClassByName("org.tlsys.Main").get();

        //assertEquals(clazz.getField("b").get().getType(), clazz);
        assertEquals(clazz.getField("c").get().getType(), clazz);

        //JavaBlock block = JavaCompiller.statement(JavaParser.parseBlock("{a = 8;}"), clazz);
    }

}
