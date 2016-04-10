package org.tlsys;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VClassLoader;
import org.tlsys.twt.compil.Compiller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class JavaSourceSet implements Compiller {


    private final ArrayList<JavaFile> files = new ArrayList<>();
    private final HashMap<CompilationUnit, JavaFile> compiled = new HashMap<>();
    private final VClassLoader classLoader;

    public JavaSourceSet(VClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void add(String name, String data) {
        files.add(new JavaFile(name, data));
    }

    @Override
    public Optional<VClass> getClass(String name) {
        return null;
    }

    @Override
    public ClassNameState compile() {
        try {
            for (JavaFile j : files) {
                InputStream stream = new ByteArrayInputStream(j.getData().getBytes(StandardCharsets.UTF_8));
                CompilationUnit cu = JavaParser.parse(stream);
                compiled.put(cu, j);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
