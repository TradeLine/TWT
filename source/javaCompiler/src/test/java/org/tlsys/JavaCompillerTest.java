package org.tlsys;

import org.junit.Test;
import org.tlsys.lex.members.MehtodSearchRequest;
import org.tlsys.lex.members.TClassLoader;
import org.tlsys.lex.members.VClass;
import org.tlsys.lex.members.VMethod;

import java.util.Optional;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class JavaCompillerTest {

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
            if (name.equals("void"))
                name = "org.tlsys.Tvoid";
            if (name.equals("int"))
                name = "org.tlsys.Tint";
            Optional<VClass> ck = super.findClassByName(name);
            if (ck.isPresent())
                return ck;

            return javaSourceSet.getClass(name);
        }
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
            if (name.equals("void"))
                name = "org.tlsys.Tvoid";
            if (name.equals("int"))
                name = "org.tlsys.Tint";
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
                .append("public static void main(){}")
                .append("}");

        VirtualFileProvider fs = new VirtualFileProvider();
        fs.getRoot().dir("org").dir("tlsys").file("Main.java", sb.toString().getBytes());


        sb = new StringBuilder();

        sb.append("package org.tlsys;").
                append("public class Tvoid {")
                .append("}");
        fs.getRoot().dir("org").dir("tlsys").file("Tvoid.java", sb.toString().getBytes());


        JClassLoader classLoader = new JClassLoader();

        JavaSourceSet com = new JavaSourceSet(classLoader, fs);
        classLoader.setJavaSourceSet(com);

        Optional<VClass> op = classLoader.findClassByName("org.tlsys.Main");

        assertNotNull(op);
        assertTrue(op.isPresent());

        Optional<VMethod> method = op.get().findMethod("main", MehtodSearchRequest.of(null));
        assertTrue(method.isPresent());

        System.out.println("" + method.get().getResult());
        System.out.println("" + method.get().getArguments());
    }
}
