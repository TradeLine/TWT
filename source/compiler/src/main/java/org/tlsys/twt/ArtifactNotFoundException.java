package org.tlsys.twt;

public class ArtifactNotFoundException extends Exception {
    private String name;
    private String group;
    private String version;

    public ArtifactNotFoundException(String name, String group, String version) {
        this.name = name;
        this.group = group;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String getMessage() {
        return "Artifact " + getGroup()+":"+getName()+":"+getVersion() + " not found";
    }
}
