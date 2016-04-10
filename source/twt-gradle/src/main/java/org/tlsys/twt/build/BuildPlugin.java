package org.tlsys.twt.build;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.tlsys.twt.compile.CompilePlugin;

public class BuildPlugin implements org.gradle.api.Plugin<Project> {
    @Override
    public void apply(Project target) {
        GenerationTask generate = target.getTasks().create("buildTwt", GenerationTask.class);
        generate.dependsOn(CompilePlugin.findAllCompileTaskInDependency(target));
        generate.mustRunAfter(target.getTasks().getByName("classes"));
        target.getTasks().getByName("assemble").dependsOn(generate);

        CompilePlugin.configConfiguration(target);
        CompilePlugin.configPlugin(target, generate);
    }
}
