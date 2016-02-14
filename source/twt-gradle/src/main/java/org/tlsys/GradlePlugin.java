package org.tlsys;

import org.gradle.api.Project;

public class GradlePlugin implements org.gradle.api.Plugin<Project> {

    @Override
    public void apply(Project target) {
        target.getExtensions().create("twt-plugin", TWTCompilePluginExtension.class);
        target.getTasks().create("twt_compile",CompileTask.class);
    }
}
