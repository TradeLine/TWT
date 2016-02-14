package org.tlsys.twt;

import org.gradle.api.Project;
import org.gradle.api.artifacts.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

    public static Collection<DClassLoader> loadClass(ArtifactRecolver artifactRecolver, DLoader loader, Dependency dependency) throws IOException {
        if (dependency instanceof ProjectDependency) {
            ProjectDependency pd = (ProjectDependency)dependency;
            Optional<DClassLoader> p = loader.getByName(Utils.getDClassLoaderName(pd.getDependencyProject()));
            if (p.isPresent())
                return Arrays.asList(p.get());
            DClassLoader cd = new GradleProjectDClassLoader(pd.getDependencyProject(), artifactRecolver, loader);
            loader.add(cd);
            return Arrays.asList(cd);
        }

        if (dependency instanceof ExternalModuleDependency) {
            ExternalModuleDependency emd  = (ExternalModuleDependency)dependency;
            ArtifactRecord ar = artifactRecolver.getArtifacte(emd.getName(), emd.getGroup(), emd.getVersion());
            return Arrays.asList(loadClass(artifactRecolver, loader, ar));
        }

        if (dependency instanceof SelfResolvingDependency) {
            SelfResolvingDependency srd = (SelfResolvingDependency)dependency;
            Set<File> file = srd.resolve();
            ArrayList<DClassLoader> out = new ArrayList<DClassLoader>(file.size());
            for (File f : file) {
                out.add(loadClass(loader, f));
            }
            return out;
        }
        throw new RuntimeException("Unknown dependency " + dependency.getClass().getName());
    }
}
