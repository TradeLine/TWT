package org.tlsys.twt;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class ExternalDClassLoader extends DClassLoader {

    private final ArtifactRecord artifactRecord;

    private final Set<DClassLoader> parents = new HashSet<>();

    private static final Logger LOG = Logger.getLogger(ExternalDClassLoader.class.getName());

    public ExternalDClassLoader(DLoader loader, ArtifactRecolver artifactRecolver, ArtifactRecord artifactRecord) {
        super(loader);
        this.artifactRecord = artifactRecord;
        try {
            addURL(artifactRecord.getJar().toURI().toURL());

            for (PomFile.Dependency d : PomFile.getDependecy(artifactRecord.getPom())) {
                if (d instanceof PomFile.ArtifactDependency) {
                    PomFile.ArtifactDependency ad = (PomFile.ArtifactDependency)d;
                    try {
                        parents.add(Utils.loadClass(artifactRecolver, loader, artifactRecolver.getArtifacte(ad.getName(), ad.getGroup(), ad.getVersion())));
                    } catch (ArtifactNotFoundException e) {
                        LOG.info(e.getMessage());
                    }
                    continue;
                }

                if (d instanceof PomFile.JarDependency) {
                    PomFile.JarDependency jd = (PomFile.JarDependency)d;
                    parents.add(Utils.loadClass(loader, jd.getJar()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Set<DClassLoader> getParents() {
        return parents;
    }

    @Override
    public String getName() {
        return artifactRecord.getJar().getAbsolutePath();
    }

}
