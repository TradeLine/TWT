package org.tlsys.twt.compiler;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.tools.javac.api.JavacTool;
import org.tlsys.twt.CompileException;

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
import org.tlsys.twt.TWTModuleSource;

public class SourceCompiler {

    public static void compile(TWTModuleSource projectClassLoader) throws IOException, CompileException {
        JavaCompiler compiler = JavacTool.create();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, Charset.forName("UTF-8"));
        ProjectFileManager projectFileManager = new ProjectFileManager(fileManager, projectClassLoader.getJavaClassLoader());

        System.out.println("SOURCE FOR COMPILE " + projectClassLoader.getSourcees());
        Iterable<? extends JavaFileObject> fileObjects = projectFileManager.getStandardFileManager().getJavaFileObjectsFromFiles(projectClassLoader.getSourcees());
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

        try {
            javacTask.analyze();

            ClassCompiler.compile(compiled, projectClassLoader.getTWTClassLoader(), e -> projectClassLoader.getTWTClassLoader().addClass(e));
        } catch (Error e) {
            throw new CompileException("Compile error", e);
        }
    }
}
