package org.tlsys;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import org.junit.Test;
import org.tlsys.java.lex.JavaBlock;
import org.tlsys.java.lex.JavaStaExpression;
import org.tlsys.java.lex.JavaVarDeclare;
import org.tlsys.lex.members.MehtodSearchRequest;
import org.tlsys.lex.members.TClassLoader;
import org.tlsys.lex.members.VClass;
import org.tlsys.lex.members.VMethod;

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

    private void addSimpleNativeClass(VirtualFileProvider fs, String name) {
        addSimpleClass(fs, "T" + name);
    }

    @Test
    public void test() {
        StringBuilder sb = new StringBuilder();

        sb.append("package org.tlsys;").
                append("public class Main {")
                .append("public static void main(){int a = 8;}")
                .append("}");

        VirtualFileProvider fs = new VirtualFileProvider();
        fs.getRoot().dir("org").dir("tlsys").file("Main.java", sb.toString().getBytes());

/*
        sb = new StringBuilder();


        sb.append("package org.tlsys;").
                append("public class Tvoid {")
                .append("}");
        fs.getRoot().dir("org").dir("tlsys").file("Tvoid.java", sb.toString().getBytes());

        */

        addSimpleNativeClass(fs, "void");


        JClassLoader classLoader = new JClassLoader();

        JavaSourceSet com = new JavaSourceSet(classLoader, fs);
        classLoader.setJavaSourceSet(com);

        Optional<VClass> op = classLoader.findClassByName("org.tlsys.Main");

        assertNotNull(op);
        assertTrue(op.isPresent());

        Optional<VMethod> method = op.get().findMethod("main", MehtodSearchRequest.of(null));
        assertTrue(method.isPresent());

        assertEquals(method.get().getResult(), classLoader.findClassByName("void").get());
        assertEquals(method.get().getArguments().size(), 0);
    }

    @Test
    public void testParseVarDeclaration() throws ParseException {
        VirtualFileProvider fs = new VirtualFileProvider();

        addSimpleNativeClass(fs, "int");

        JClassLoader classLoader = new JClassLoader();

        JavaSourceSet com = new JavaSourceSet(classLoader, fs);
        classLoader.setJavaSourceSet(com);

        VClass clazz = classLoader.findClassByName(addSimpleClass(fs, "Test")).get();

        JavaBlock block = JavaCompiller.statement(JavaParser.parseBlock("{int a=8;}"), clazz);
        JavaStaExpression exp = (JavaStaExpression) block.getStatement(0);
        JavaVarDeclare jvd = (JavaVarDeclare) exp.getExpression();
        assertEquals(jvd.getVars().size(), 1);
        assertEquals(jvd.getVars().get(0).getType(), classLoader.findClassByName("int").get());
    }

    @Test
    public void testParseVarSet() throws ParseException {
        VirtualFileProvider fs = new VirtualFileProvider();

        addSimpleNativeClass(fs, "int");

        JClassLoader classLoader = new JClassLoader();

        JavaSourceSet com = new JavaSourceSet(classLoader, fs);
        classLoader.setJavaSourceSet(com);

        VClass clazz = classLoader.findClassByName(addSimpleClass(fs, "Test")).get();

        JavaBlock block = JavaCompiller.statement(JavaParser.parseBlock("{int a = 10; a = 8;}"), clazz);
    }

    private static class JClassLoader extends TClassLoader {
        private JavaSourceSet javaSourceSet;

        private JClassLoader() {
        }

        public JavaSourceSet getJavaSourceSet() {
            return javaSourceSet;
        }

        public void setJavaSourceSet(JavaSourceSet javaSourceSet) {
            this.javaSourceSet = javaSourceSet;
        }

        @Override
        public Optional<VClass> findClassByName(String name) {
            if (!name.contains(".")) {
                Optional<VClass> o = findClassByName("org.tlsys.T" + name);
                if (o.isPresent())
                    return o;
            }

            Optional<VClass> ck = super.findClassByName(name);
            if (ck.isPresent())
                return ck;

            return javaSourceSet.getClass(name);
        }
    }
}
