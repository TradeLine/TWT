package org.tlsys.twt.compiler;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.tools.javac.api.JavacTool;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.SourceClassLoader;

import javax.lang.model.util.Types;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SourceCompiler {
    public static void compile(SourceClassLoader projectClassLoader) throws IOException, CompileException {
        System.out.println("Try compile project...");
        JavaCompiler compiler = JavacTool.create();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, Charset.forName("UTF-8"));
        ProjectFileManager projectFileManager = new ProjectFileManager(fileManager, projectClassLoader);

        System.out.println("SOURCES=" + projectClassLoader.getSourceFiles());
        Iterable<? extends JavaFileObject> fileObjects = projectFileManager.getStandardFileManager().getJavaFileObjectsFromFiles(projectClassLoader.getSourceFiles());
        List<String> options = Arrays.asList("-proc:none");
        JavaCompiler.CompilationTask task = compiler.getTask(null, projectFileManager, null, options, null, fileObjects);
        JavacTask javacTask = (JavacTask) task;
        Types types = javacTask.getTypes();
        Iterator<? extends CompilationUnitTree> it = javacTask.parse().iterator();
        List<CompilationUnitTree> compiled = new ArrayList<>();
        while (it.hasNext()) {
            CompilationUnitTree cu = it.next();
            compiled.add(cu);
        }

        javacTask.analyze();

        ClassCompiler.compile(compiled, projectClassLoader.getJsClassLoader(), e->projectClassLoader.getJsClassLoader().addClass(e));
    }
}
