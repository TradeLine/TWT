package org.tlsys;

import org.gradle.api.DefaultTask;

public class TWTPluginExtension {
    private String sampleFilePath="/home/mahendra/abc";
    public void setSampleFilePath(String sampleFilePath){
        this.sampleFilePath=sampleFilePath;
    }
    public String getSampleFilePath(){
        return sampleFilePath;
    }
}
