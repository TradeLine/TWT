/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tlsys.twt;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.tlsys.lex.declare.VClassLoader;
import org.tlsys.twt.compiler.SourceFinder;

/**
 *
 * @author subochev
 */
public class GradleTWTSourceProject extends TWTModuleSource {

    private final String name;
    
      public static Set<File> findSource(File classes, File source) throws IOException {
        return SourceFinder.getCompileClasses(classes, name -> {
            String classFilePath = name.replace('.', File.separatorChar) + ".java";
            File f = new File(source + File.separator + classFilePath);
            if (f.isFile()) {
                return Optional.of(f);
            }
            return Optional.empty();
        });
    }

    public GradleTWTSourceProject(Project project, ArtifactRecolver artifactRecolver, DLoader loader) throws IOException {
        super(new File(project.getBuildDir()+ File.separator+"classes" + File.separator + "main"),
                new File(project.getProjectDir()+ File.separator+"src" + File.separator + "main" + File.separator + "java"));
        name = project.getGroup() + "-" + project.getName();
        loader.add(this);
        
        for (Configuration c : project.getConfigurations()) {
            for (Dependency d : c.getDependencies()) {
                for (TWTModule m : Utils.loadClass(artifactRecolver, loader, d)) {
                    addParent(m);
                }
            }
        }
        
        VClassLoader cl = new VClassLoader(getName());
        cl.setJavaClassLoader(getJavaClassLoader());
        setTWTClassLoader(cl);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
}
