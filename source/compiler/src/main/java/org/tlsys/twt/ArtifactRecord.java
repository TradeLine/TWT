package org.tlsys.twt;

import java.io.File;

public class ArtifactRecord {
    private final File jar;
    private final File pom;

    public ArtifactRecord(File jar, File pom) {
        this.jar = jar;
        this.pom = pom;
    }

    public File getJar() {
        return jar;
    }

    public File getPom() {
        return pom;
    }
}
