package org.tlsys.twt.compile;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ProjectDependency;

import java.util.HashSet;
import java.util.Set;

public class CompilePlugin implements org.gradle.api.Plugin<Project> {
    @Override
    public void apply(Project target) {
        Task compile = target.getTasks().create("compileTwt",CompileTask.class);
        compile.dependsOn(findAllCompileTaskInDependency(target));
        target.getTasks().getByName("jar").dependsOn(compile);
    }

    public static Set<Task> findAllCompileTaskInDependency(Project target) {
        HashSet<Task> out = new HashSet<>();
        for (Configuration c : target.getConfigurations()) {
            for (Dependency d : c.getDependencies()) {
                if (d instanceof ProjectDependency) {
                    ProjectDependency pd = (ProjectDependency)d;
                    try {
                        out.add(pd.getDependencyProject().getTasks().getByName("compileTWT"));
                    } catch (UnknownTaskException e) {

                    }
                }
            }
        }
        return out;
    }
}
