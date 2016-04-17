package org.tlsys;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.junit.Test;
import org.tlsys.java.lex.JavaStaExpression;
import org.tlsys.java.lex.JavaVarDeclare;
import org.tlsys.lex.members.MehtodSearchRequest;
import org.tlsys.lex.members.TClassLoader;
import org.tlsys.lex.members.VClass;
import org.tlsys.lex.members.VMethod;

import java.util.Optional;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class JavaCompillerTest {

    private void addSimpleClass(VirtualFileProvider fs, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("package org.tlsys;").
                append("public class T"+name+" {")
                .append("}");
        fs.getRoot().dir("org").dir("tlsys").file("T"+name+".java", sb.toString().getBytes());
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
            if (name.equals("void")) {
                Optional<VClass> o = super.findClassByName("org.tlsys.T" + name);
                if (o.isPresent())
                    return o;
            }
            Optional<VClass> ck = super.findClassByName(name);
            if (ck.isPresent())
                return ck;

            return javaSourceSet.getClass(name);
        }
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

        addSimpleClass(fs, "void");


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

        JClassLoader classLoader = new JClassLoader();

        JavaSourceSet com = new JavaSourceSet(classLoader, fs);
        classLoader.setJavaSourceSet(com);

        Statement st = JavaParser.parseBlock("{int a=8;}").getStmts().get(0);
        JavaVarDeclare jvd = (JavaVarDeclare) ((JavaStaExpression)JavaCompiller.statement(st, null)).getExpression();
        assertEquals(jvd.getVars().size(), 1);
    }
}
