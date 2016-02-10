package org.tlsys;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ProjectDependency;
import org.tlsys.twt.DClassLoader;
import org.tlsys.twt.DLoader;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GradleProjectDClassLoader extends GradleDClassLoader {

    private Set<DClassLoader> parents = new HashSet<>();

    public GradleProjectDClassLoader(Project project, DLoader loader) {
        super(Utils.getDClassLoaderName(project), loader);
        for (Configuration c : project.getConfigurations()) {
            for (Dependency d : c.getDependencies()) {
                if (d instanceof ProjectDependency) {
                    ProjectDependency pd = (ProjectDependency)d;
                    Optional<DClassLoader> p = getLoader().getByName(Utils.getDClassLoaderName(pd.getDependencyProject()));
                    if (p.isPresent())
                        parents.add(p.get());
                    else {
                        DClassLoader cd = new GradleProjectDClassLoader(pd.getDependencyProject(), getLoader());
                        getLoader().add(cd);
                        parents.add(cd);
                    }
                    continue;
                }
                throw new RuntimeException("Unknown dependency " + d.getClass().getName());
            }
        }
    }

    @Override
    public Set<DClassLoader> getParents() {
        return null;
    }
}
