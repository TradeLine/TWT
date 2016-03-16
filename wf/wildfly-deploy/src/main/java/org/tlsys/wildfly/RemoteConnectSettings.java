package org.tlsys.wildfly;

import org.tlsys.wildfly.remote.RemoteConnector;

import java.io.IOException;
import java.net.UnknownHostException;

public class RemoteConnectSettings implements ConnectSettings {
    private final String user;
    private final String password;
    private final String host;
    private final int port;

    public RemoteConnectSettings(String user, String password, String host, int port) {
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public Connector createConnector() throws IOException {
        return new RemoteConnector(getUser(), getPassword(), getHost(), getPort());
    }
}
