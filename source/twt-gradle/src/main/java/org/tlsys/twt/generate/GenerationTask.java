package org.tlsys.twt.generate;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenLocalArtifactRepository;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.tlsys.twt.*;
import org.tlsys.twt.compile.TWTCompilePluginExtension;
import org.tlsys.twt.compiler.SourceCompiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Optional;

public class GenerationTask extends DefaultTask {

    @TaskAction
    public void samplePluginTasks() throws TaskExecutionException {
        DLoader loader = new DLoader();

        ArtifactRecolver artifactRecolver = new ArtifactRecolver();
        RepositoryHandler rh = getProject().getRepositories();
        for (ArtifactRepository ar :rh) {
            if (ar instanceof DefaultMavenLocalArtifactRepository) {
                DefaultMavenLocalArtifactRepository mar = (DefaultMavenLocalArtifactRepository)ar;
                artifactRecolver.addPath(new File(mar.getUrl()));
            }
        }

        TWTCompilePluginExtension extension = getProject().getExtensions().findByType(TWTCompilePluginExtension.class);

        FileCollection source = getInputs().getSourceFiles();
        JavaPluginConvention javaConvention = getProject().getConvention().getPlugin(JavaPluginConvention.class);
        SourceSetContainer ssc = javaConvention.getSourceSets();
        SourceSet ss = ssc.getByName(extension.getSourceName());

        Optional<File> sources = ss.getAllSource().getSrcDirs().stream().filter(e -> e.getName().equals("java")).findFirst();
        if (!sources.isPresent())
            throw new TaskExecutionException(this, new RuntimeException("Can't find directore with java source"));

        try {
            File classDir = new File(getProject().getBuildDir().getAbsolutePath()+File.separator+"classes"+File.separator+extension.getSourceName());
            ProjectSourceDClassLoader mainLoader = new ProjectSourceDClassLoader(sources.get(), classDir, artifactRecolver, loader, getProject());
            loader.add(mainLoader);

            SourceCompiler.compile(mainLoader);

            TWTGenerationPluginExtension genPropertys = getProject().getExtensions().findByType(TWTGenerationPluginExtension.class);
            for (GenerationTarget gt : genPropertys.getTargets()) {
                try (PrintStream ps = new PrintStream(new FileOutputStream(gt.fileName()))) {
                    CompileModuls cm = new CompileModuls();
                    for (String c : gt.getClasses()) {
                        cm.add(mainLoader.getJsClassLoader().loadClass(c));
                    }

                    Class cl = mainLoader.loadClass(gt.generator());
                    MainGenerator mg = (MainGenerator) cl.newInstance();
                    mg.generate(mainLoader.getJsClassLoader(), cm, ps);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(new File(classDir+File.separator+ DClassLoader.JSLIB))) {
                mainLoader.saveJSClassLoader(fos);
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new TaskExecutionException(this,new Exception("Exception occured while processing sampleTask",e));
        }
    }
}
