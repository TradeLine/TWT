package org.tlsys.twt.compile;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.tlsys.twt.TWTPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public class CompilePlugin implements org.gradle.api.Plugin<Project> {

    public static final String TWT_COMPILE_CONFIGURATION = "twtCompile";
    public static final String TWT_GROUP = "TWT";

    @Override
    public void apply(Project target) {
        CompileTask compile = target.getTasks().create("compileTwt",CompileTask.class);
        compile.dependsOn(findAllCompileTaskInDependency(target));
        target.getTasks().getByName("jar").dependsOn(compile);

        configConfiguration(target);
        configPlugin(target, compile);
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

    public static void configPlugin(Project project, TWTPlugin task) {
        task.setGroup(CompilePlugin.TWT_GROUP);
        task.classpath(new Callable() {
            public Object call() throws Exception {
                FileCollection runtimeClasspath = project.getConvention().getPlugin(JavaPluginConvention.class)
                        .getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME).getRuntimeClasspath();
                Configuration providedRuntime = project.getConfigurations().getByName(
                        TWT_COMPILE_CONFIGURATION);
                return providedRuntime;
                //return runtimeClasspath.minus(providedRuntime);
            }
        });
    }

    public static Configuration configConfiguration(Project target) {
        //System.out.println("Added config to " + target + "...");
        Configuration conf = target.getConfigurations().create(TWT_COMPILE_CONFIGURATION).setVisible(false)
                .setDescription("TWT Compile Libs");
        target.getConfigurations().getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME).extendsFrom(conf);
        return conf;
    }
}
