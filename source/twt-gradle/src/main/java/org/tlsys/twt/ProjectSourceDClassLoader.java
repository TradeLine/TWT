package org.tlsys.twt;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.tlsys.lex.declare.VClassLoader;
import org.tlsys.twt.compiler.SourceFinder;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ProjectSourceDClassLoader extends SourceClassLoader {

    private final Project project;
    private final Set<File> sourcees;

    private Set<DClassLoader> parents = new HashSet<>();

    public ProjectSourceDClassLoader(File sourceDir, File classDir, ArtifactRecolver artifactRecolver, DLoader loader, Project project) throws IOException {
        super(loader);
        this.project = project;
        System.out.println("Source = " + sourceDir);


        sourcees = SourceFinder.getCompileClasses(classDir, name -> {
            String classFilePath = name.replace('.', File.separatorChar) + ".java";
            File f = new File(sourceDir + File.separator + classFilePath);
            if (f.isFile()) {
                return Optional.of(f);
            }
            return Optional.empty();
        });


        for (Configuration c : project.getConfigurations()) {
            for (Dependency d : c.getDependencies()) {
                parents.addAll(Utils.loadClass(artifactRecolver, getLoader(), d));
            }
        }
        VClassLoader cl = new VClassLoader(getName());
        for (DClassLoader c : getParents()) {
            VClassLoader l = c.getJsClassLoader();
            if (l != null)
                cl.parents.add(l);
        }
        setJsClassLoader(cl);
        cl.setJavaClassLoader(this);

        addURL(classDir.toURI().toURL());
    }

    @Override
    public Set<File> getSourceFiles() {
        return sourcees;
    }

    @Override
    public Set<DClassLoader> getParents() {
        return parents;
    }

    @Override
    public String getName() {
        return project.getGroup()+"-"+project.getName()+"-"+project.getVersion();
    }
}
