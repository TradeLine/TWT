package org.tlsys;

import org.gradle.api.Project;

public class GradlePlugin implements org.gradle.api.Plugin<Project> {

    @Override
    public void apply(Project target) {
        target.getExtensions().create("twt-plugin", TWTPluginExtension.class);
        target.getTasks().create("demo",GenerationTask.class);
    }
}
