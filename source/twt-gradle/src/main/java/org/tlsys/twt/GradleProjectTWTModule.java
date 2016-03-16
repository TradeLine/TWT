/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tlsys.twt;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;

/**
 * @author subochev
 */
public class GradleProjectTWTModule extends GradleJarModule {

    public GradleProjectTWTModule(Project project, ArtifactRecolver artifactRecolver, DLoader loader) throws IOException {
        super(new File(project.getBuildDir() + File.separator + "classes" + File.separator + "main"),
                new File(project.getBuildDir() + File.separator + "resources" + File.separator + "main"),
                project.getGroup() + "-" + project.getName());
        URLClassLoader cl = getJavaClassLoader();
        loader.add(this);

        for (Configuration c : project.getConfigurations()) {
            for (Dependency d : c.getDependencies()) {
                for (TWTModule m : Utils.loadClass(artifactRecolver, loader, d)) {
                    addParent(m);
                }
            }
        }
    }

}
