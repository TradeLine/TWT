package org.tlsys;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.artifacts.dsl.ComponentMetadataHandler;
import org.gradle.api.artifacts.dsl.ComponentModuleMetadataHandler;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.tlsys.twt.DLoader;

import java.util.Set;
import java.util.logging.Logger;

public class GenerationTask extends DefaultTask {

    private static final Logger LOG = Logger.getLogger(GenerationTask.class.getName());

    @TaskAction
    public void samplePluginTasks() throws TaskExecutionException {
        System.out.println("TASK");
        DLoader loader = new DLoader();
        GradleProjectDClassLoader mainLoader = new GradleProjectDClassLoader(getProject(), loader);

        LOG.info("Starting sample task");
        try {
            /*
            ConfigurationContainer cc = getProject().getConfigurations();
            for (Configuration c : cc) {
                System.out.println("CONFIGURE " + c.toString());
                for(Dependency d : c.getAllDependencies()) {
                    System.out.print("DEPENDENCY " + d);
                    if (d instanceof ProjectDependency) {
                        System.out.print("- PROJECT");

                        ProjectDependency pd = (ProjectDependency)d;
                        System.out.println(pd.getDependencyProject().getBuildDir().getAbsolutePath());
                    } else
                        System.out.println("-UNKNOWN");
                }
                System.out.println("\n\n");
            }
            Set<Project> projects = getProject().getAllprojects();
            DependencyHandler dh = getProject().getDependencies();
            ComponentModuleMetadataHandler cmm = dh.getModules();
            ComponentMetadataHandler cmh = dh.getComponents();
            System.out.println("->" + dh + cmm + cmh + cc);
            */
            TWTPluginExtension extension = getProject().getExtensions().findByType(TWTPluginExtension.class);
            String filePath = extension.getSampleFilePath();
            LOG.info("Sample file path is: " + filePath);
            LOG.info("Successfully completed sample Task");
        }catch(Exception e){
            LOG.info("ERROR");
            e.printStackTrace();
            throw new TaskExecutionException(this,new Exception("Exception occured while processing sampleTask",e));
        }
    }
}
