package org.tlsys;

import org.gradle.api.DefaultTask;

import java.io.File;

public class TWTPluginExtension {
    private String sourceName="main";

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
