package org.tlsys.twt;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenLocalArtifactRepository;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.tlsys.twt.compiler.SourceCompiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Субочев Антон on 26.02.2016.
 */
public class AppCompiller {

    public static File getClassDir(Project project) {
        return new File(project.getBuildDir().getAbsolutePath() + File.separator + "classes" + File.separator + "main");
    }

    public static Set<File> getSourceOfProject(Project project) {
        JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        SourceSetContainer ssc = javaConvention.getSourceSets();
        SourceSet ss = ssc.getByName("main");
        HashSet<File> out = new HashSet<>();
        for (File f : ss.getJava()) {
            out.add(f);
        }
        return out;
    }

    public static App compileApp(Task task) throws IOException, CompileException {
        DLoader loader = new DLoader();
        try {
            try {
                ArtifactRecolver artifactRecolver = new ArtifactRecolver();
                RepositoryHandler rh = task.getProject().getRepositories();
                for (ArtifactRepository ar : rh) {
                    if (ar instanceof DefaultMavenLocalArtifactRepository) {
                        DefaultMavenLocalArtifactRepository mar = (DefaultMavenLocalArtifactRepository) ar;
                        artifactRecolver.addPath(new File(mar.getUrl()));
                    }
                }

                FileCollection source = task.getInputs().getSourceFiles();
                JavaPluginConvention javaConvention = task.getProject().getConvention().getPlugin(JavaPluginConvention.class);
                SourceSetContainer ssc = javaConvention.getSourceSets();
                SourceSet ss = ssc.getByName("main");

                Optional<File> sources = ss.getAllSource().getSrcDirs().stream().filter(e -> e.getName().equals("java")).findFirst();
                if (!sources.isPresent()) {
                    throw new IOException("Can't find directore with java source");
                }

                File classDir = getClassDir(task.getProject());
                GradleTWTSourceProject mainLoader = new GradleTWTSourceProject(task.getProject(), artifactRecolver, loader);
                //ProjectSourceDClassLoader mainLoader = new ProjectSourceDClassLoader(sources.get(), classDir, artifactRecolver, loader, task.getProject());

                ArrayList<File> pathes = new ArrayList<>();
                for (Configuration c : task.getProject().getConfigurations()) {
                    for (Dependency d : c.getDependencies()) {
                        if (d instanceof ProjectDependency) {
                            ProjectDependency pd = (ProjectDependency) d;
                            pathes.add(new File(pd.getDependencyProject().getBuildDir() + File.separator + "classes" + File.separator + "main"));
                        }
                    }
                }

                for (File f : task.getProject().getConfigurations().getByName("compile")) {
                    pathes.add(f);
                }

                //LibLoader.loadLibsFor(mainLoader, pathes);
                loader.add(mainLoader);
                //ModuleInfo mi = new ModuleInfo(mainLoader.getName(), LibLoader.getAllDependencys(mainLoader));
                SourceCompiler.compile(mainLoader);

                return new App(loader, classDir, mainLoader/*, mi*/);
            } catch (Throwable e) {
                e.printStackTrace();
                for (TWTModule cl : loader.getLoaders()) {
                    try {
                        cl.getJavaClassLoader().close();
                    } catch (IOException io) {
                        //ignore
                    }
                }
                throw e;
            }
        } catch (Throwable ee) {
            AppCompiller.closeLoaded(loader);
            throw new RuntimeException(ee);
        }
    }

    public static void closeLoaded(DLoader loader) {
        for (TWTModule cl : loader.getLoaders()) {
            try {
                cl.getJavaClassLoader().clearAssertionStatus();
                cl.getJavaClassLoader().close();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    public static class App {

        private final DLoader loader;
        private final File classDir;
        private final GradleTWTSourceProject mainLoader;
        //private final ModuleInfo info;

        public App(DLoader loader, File classDir, GradleTWTSourceProject mainLoader/*, ModuleInfo info*/) {
            this.loader = loader;
            this.classDir = classDir;
            this.mainLoader = mainLoader;
            //this.info = info;
        }

        /*
        public ModuleInfo getInfo() {
            return info;
        }
*/


        public GradleTWTSourceProject getMainLoader() {
            return mainLoader;
        }

        public File getClassDir() {
            return classDir;
        }

        public void close() {
            closeLoaded(loader);
        }
    }
}
