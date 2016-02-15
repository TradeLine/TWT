package org.tlsys.wildfly;

import java.io.File;

public class Target {
    public File file;

    public File file() {
        return file;
    }

    public Target file(File file) {
        this.file = file;
        return this;
    }
}
