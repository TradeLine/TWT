package org.tlsys;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ProjectDependency;
import org.tlsys.twt.ArtifactRecolver;
import org.tlsys.twt.DClassLoader;
import org.tlsys.twt.DLoader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GradleProjectDClassLoader extends GradleDClassLoader {

    private Set<DClassLoader> parents = new HashSet<>();

    public GradleProjectDClassLoader(Project project, ArtifactRecolver artifactRecolver, DLoader loader) throws IOException {
        super(Utils.getDClassLoaderName(project), loader);

        addURL(new File(project.getBuildDir()+ File.separator+"classes" + File.separator + "main").toURI().toURL());

        for (Configuration c : project.getConfigurations()) {
            for (Dependency d : c.getDependencies()) {
                parents.addAll(Utils.loadClass(artifactRecolver, getLoader(), d));
            }
        }
    }

    @Override
    public Set<DClassLoader> getParents() {
        return parents;
    }
}
