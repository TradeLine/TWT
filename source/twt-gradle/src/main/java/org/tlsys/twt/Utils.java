package org.tlsys.twt;

import org.gradle.api.Project;
import org.gradle.api.artifacts.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
final class Utils {
    private Utils() {
    }

    public static String getDClassLoaderName(Project project) {
        return project.getName()+"-"+project.getName()+"-"+project.getVersion();
    }

    public static TWTModule loadClass(ArtifactRecolver artifactRecolver, DLoader loader, ArtifactRecord artifactRecord) throws IOException {
        Optional<TWTModule> res = loader.getByName(artifactRecord.getJar().getAbsolutePath());
        if (res.isPresent())
            return res.get();
        GradleExternalModule dd = new GradleExternalModule(loader, artifactRecolver, artifactRecord);
        loader.add(dd);
        return dd;
    }

    public static TWTModule loadClass(DLoader loader, File jarFile) throws IOException {
        Optional<TWTModule> res = loader.getByName(jarFile.getAbsolutePath());
        if (res.isPresent())
            return res.get();
        GradleJarModule d = new GradleJarModule(jarFile, null, jarFile.getAbsolutePath());
        loader.add(d);
        return d;
    }

    private static final Logger LOG = Logger.getLogger(Utils.class.getName());

    public static Collection<TWTModule> loadClass(ArtifactRecolver artifactRecolver, DLoader loader, Dependency dependency) throws IOException {
        if (dependency instanceof ProjectDependency) {
            ProjectDependency pd = (ProjectDependency)dependency;
            Optional<TWTModule> p = loader.getByName(Utils.getDClassLoaderName(pd.getDependencyProject()));
            if (p.isPresent())
                return Arrays.asList(p.get());
            GradleProjectTWTModule mod = new GradleProjectTWTModule(pd.getDependencyProject(), artifactRecolver, loader);
            loader.add(mod);
            return Arrays.asList(mod);
        }

        if (dependency instanceof ExternalModuleDependency) {
            ExternalModuleDependency emd  = (ExternalModuleDependency)dependency;
            ArtifactRecord ar = null;
            try {
                ar = artifactRecolver.getArtifacte(emd.getName(), emd.getGroup(), emd.getVersion());
            } catch (ArtifactNotFoundException e) {
                LOG.info(e.getMessage());
                return Collections.EMPTY_LIST;
            }
            return Arrays.asList(loadClass(artifactRecolver, loader, ar));
        }

        if (dependency instanceof SelfResolvingDependency) {
            SelfResolvingDependency srd = (SelfResolvingDependency)dependency;
            Set<File> file = srd.resolve();
            ArrayList<TWTModule> out = new ArrayList<>(file.size());
            for (File f : file) {
                out.add(loadClass(loader, f));
            }
            return out;
        }
        throw new RuntimeException("Unknown dependency " + dependency.getClass().getName());
    }
}
