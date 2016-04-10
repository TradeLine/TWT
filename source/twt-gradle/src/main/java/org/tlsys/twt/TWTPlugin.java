package org.tlsys.twt;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;

public class TWTPlugin extends DefaultTask {

    private FileCollection classpath;

    @InputFiles
    @Optional
    public FileCollection getClasspath() {
        return classpath;
    }

    public void setClasspath(Object classpath) {
        this.classpath = getProject().files(classpath);
    }

    public void classpath(Object... classpath) {
        FileCollection oldClasspath = getClasspath();
        this.classpath = getProject().files(classpath);
    }
}
