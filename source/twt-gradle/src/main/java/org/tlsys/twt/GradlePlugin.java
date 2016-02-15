package org.tlsys.twt;

import org.gradle.api.Project;
import org.tlsys.twt.compile.CompileTask;
import org.tlsys.twt.compile.TWTCompilePluginExtension;
import org.tlsys.twt.generate.GenerationTask;
import org.tlsys.twt.generate.TWTGenerationPluginExtension;

public class GradlePlugin implements org.gradle.api.Plugin<Project> {

    @Override
    public void apply(Project target) {
        target.getExtensions().create("twt-compile", TWTCompilePluginExtension.class);
        target.getExtensions().create("TWT", TWTGenerationPluginExtension.class);
        target.getTasks().create("compileTWT",CompileTask.class);

        target.getTasks().create("generateTWT", GenerationTask.class);
    }
}
