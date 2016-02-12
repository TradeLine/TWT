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
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.file.DirectoryTree;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenLocalArtifactRepository;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.tlsys.twt.ArtifactRecolver;
import org.tlsys.twt.DLoader;
import org.tlsys.twt.compiler.SourceCompiler;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public class GenerationTask extends DefaultTask {

    private static final Logger LOG = Logger.getLogger(GenerationTask.class.getName());

    @TaskAction
    public void samplePluginTasks() throws TaskExecutionException {
        System.out.println("TASK123456");
        DLoader loader = new DLoader();

        ArtifactRecolver artifactRecolver = new ArtifactRecolver();
        RepositoryHandler rh = getProject().getRepositories();
        for (ArtifactRepository ar :rh) {
            if (ar instanceof DefaultMavenLocalArtifactRepository) {
                DefaultMavenLocalArtifactRepository mar = (DefaultMavenLocalArtifactRepository)ar;
                artifactRecolver.addPath(new File(mar.getUrl()));
            }
        }

        TWTPluginExtension extension = getProject().getExtensions().findByType(TWTPluginExtension.class);

        FileCollection source = getInputs().getSourceFiles();
        JavaPluginConvention javaConvention = getProject().getConvention().getPlugin(JavaPluginConvention.class);
        SourceSetContainer ssc = javaConvention.getSourceSets();
        SourceSet ss = ssc.getByName(extension.getSourceName());

        Optional<File> sources = ss.getAllSource().getSrcDirs().stream().filter(e -> e.getName().equals("java")).findFirst();
        if (!sources.isPresent())
            throw new TaskExecutionException(this, new RuntimeException("Can't find directore with java source"));


        //GradleProjectDClassLoader mainLoader = new GradleProjectDClassLoader(getProject(), artifactRecolver, loader);


        try {
            File classDir = new File(getProject().getBuildDir().getAbsolutePath()+File.separator+"classes"+File.separator+extension.getSourceName());
            ProjectSourceDClassLoader mainLoader = new ProjectSourceDClassLoader(sources.get(), classDir, artifactRecolver, loader, getProject());
            loader.add(mainLoader);

            SourceCompiler.compile(mainLoader);
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

            LOG.info("Successfully completed sample Task");
        }catch(Exception e){
            LOG.info("ERROR");
            e.printStackTrace();
            throw new TaskExecutionException(this,new Exception("Exception occured while processing sampleTask",e));
        }
    }
}
