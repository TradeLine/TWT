/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tlsys.twt;

import java.io.IOException;

/**
 *
 * @author subochev
 */
public class GradleExternalModule extends TWTModuleLoader {

    private final String name;
    
    public GradleExternalModule(DLoader loader, ArtifactRecolver artifactRecolver, ArtifactRecord artifactRecord) throws IOException {
        super(artifactRecord.getJar(), null);
        name = artifactRecord.getJar().getAbsolutePath();
        loader.add(this);

        for (PomFile.Dependency d : PomFile.getDependecy(artifactRecord.getPom())) {
            if (d instanceof PomFile.ArtifactDependency) {
                PomFile.ArtifactDependency ad = (PomFile.ArtifactDependency) d;
                try {
                    addParent(Utils.loadClass(artifactRecolver, loader, artifactRecolver.getArtifacte(ad.getName(), ad.getGroup(), ad.getVersion())));
                } catch (ArtifactNotFoundException e) {
                    //throw new RuntimeException(e);
                }
                continue;
            }

            if (d instanceof PomFile.JarDependency) {
                PomFile.JarDependency jd = (PomFile.JarDependency) d;
                addParent(Utils.loadClass(loader, jd.getJar()));
            }
        }

    }

    @Override
    public String getName() {
        return name;
    }
}
