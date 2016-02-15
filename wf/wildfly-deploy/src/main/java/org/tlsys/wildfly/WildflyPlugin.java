package org.tlsys.wildfly;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class WildflyPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getExtensions().create("wildfly", WildflySettings.class);
        target.getTasks().create("deployWildfly", DeployTask.class);
    }
}
