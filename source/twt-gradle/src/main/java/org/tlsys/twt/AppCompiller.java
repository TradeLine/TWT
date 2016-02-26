package org.tlsys.twt;

import org.gradle.api.Project;
import org.gradle.api.Task;
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
import java.util.Optional;

/**
 * Created by Субочев Антон on 26.02.2016.
 */
public class AppCompiller {

    public static File getClassDir(Project project) {
        return new File(project.getBuildDir().getAbsolutePath() + File.separator + "classes" + File.separator + "main");
    }

    public static App compileApp(Task task) throws IOException, CompileException {
        DLoader loader = new DLoader();
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
            if (!sources.isPresent())
                throw new IOException("Can't find directore with java source");

            File classDir = getClassDir(task.getProject());
            ProjectSourceDClassLoader mainLoader = new ProjectSourceDClassLoader(sources.get(), classDir, artifactRecolver, loader, task.getProject());
            loader.add(mainLoader);

            SourceCompiler.compile(mainLoader);

            return new App(loader, classDir, mainLoader);
        } catch (Throwable e){
            for(DClassLoader cl : loader.getLoaders()) {
                try {
                    cl.close();
                } catch (IOException io) {
                    //ignore
                }
            }
            throw e;
        }
    }

    public static class App {
        private final DLoader loader;
        private final File classDir;
        private final ProjectSourceDClassLoader mainLoader;

        public App(DLoader loader, File classDir, ProjectSourceDClassLoader mainLoader) {
            this.loader = loader;
            this.classDir = classDir;
            this.mainLoader = mainLoader;
        }

        public ProjectSourceDClassLoader getMainLoader() {
            return mainLoader;
        }

        public File getClassDir() {
            return classDir;
        }

        public void close() {
            for(DClassLoader cl : loader.getLoaders()) {
                try {
                    cl.close();
                } catch (IOException io) {
                    //ignore
                }
            }
        }
    }
}
