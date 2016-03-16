package org.tlsys.wildfly;

import java.io.File;

public class WildflySettings {
    private ConnectSettings connector;
    private String name;
    private File file;

    public ConnectSettings getConnector() {
        return connector;
    }

    public WildflySettings remote(String user, String password, String host, int port) {
        connector = new RemoteConnectSettings(user, password, host, port);
        return this;
    }

    public String name() {
        if (name == null)
            return file().getName();
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public File file() {
        return file;
    }

    public void file(File file) {
        this.file = file;
    }
}
