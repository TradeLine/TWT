package org.tlsys;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencyArtifact;
import org.gradle.api.artifacts.ExternalModuleDependency;
import org.gradle.api.artifacts.ProjectDependency;
import org.tlsys.twt.*;

import java.io.File;
import java.util.Optional;
import java.util.Set;

final class Utils {
    private Utils() {
    }

    public static String getDClassLoaderName(Project project) {
        return project.getName()+"-"+project.getName()+"-"+project.getVersion();
    }

    public static DClassLoader loadClass(ArtifactRecolver artifactRecolver, DLoader loader, ArtifactRecord artifactRecord) {
        Optional<DClassLoader> res = loader.getByName(artifactRecord.getJar().getAbsolutePath());
        if (res.isPresent())
            return res.get();
        ExternalDClassLoader dd = new ExternalDClassLoader(loader, artifactRecolver, artifactRecord);
        loader.add(dd);
        return dd;
    }

    public static DClassLoader loadClass(DLoader loader, File jarFile) {
        Optional<DClassLoader> res = loader.getByName(jarFile.getAbsolutePath());
        if (res.isPresent())
            return res.get();
        JarDClassLoader d = new JarDClassLoader(loader, jarFile, jarFile.getAbsolutePath());
        loader.add(d);
        return d;
    }

    public static DClassLoader loadClass(ArtifactRecolver artifactRecolver, DLoader loader, Dependency dependency) {
        if (dependency instanceof ProjectDependency) {
            ProjectDependency pd = (ProjectDependency)dependency;
            Optional<DClassLoader> p = loader.getByName(Utils.getDClassLoaderName(pd.getDependencyProject()));
            if (p.isPresent())
                return p.get();
            DClassLoader cd = new GradleProjectDClassLoader(pd.getDependencyProject(), artifactRecolver, loader);
            loader.add(cd);
            return cd;
        }

        if (dependency instanceof ExternalModuleDependency) {
            ExternalModuleDependency emd  = (ExternalModuleDependency)dependency;
            ArtifactRecord ar = artifactRecolver.getArtifacte(emd.getName(), emd.getGroup(), emd.getVersion());
            return loadClass(artifactRecolver, loader, ar);
        }
        throw new RuntimeException("Unknown dependency " + dependency.getClass().getName());
    }
}
