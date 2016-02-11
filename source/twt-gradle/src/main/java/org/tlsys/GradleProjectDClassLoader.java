package org.tlsys;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ProjectDependency;
import org.tlsys.twt.ArtifactRecolver;
import org.tlsys.twt.DClassLoader;
import org.tlsys.twt.DLoader;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GradleProjectDClassLoader extends GradleDClassLoader {

    private Set<DClassLoader> parents = new HashSet<>();

    public GradleProjectDClassLoader(Project project, ArtifactRecolver artifactRecolver, DLoader loader) {
        super(Utils.getDClassLoaderName(project), loader);
        for (Configuration c : project.getConfigurations()) {
            for (Dependency d : c.getDependencies()) {
                parents.add(Utils.loadClass(artifactRecolver, getLoader(), d));
            }
        }
    }

    @Override
    public Set<DClassLoader> getParents() {
        return null;
    }
}
