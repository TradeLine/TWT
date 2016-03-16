package org.tlsys.wildfly;

import java.io.IOException;
import java.net.UnknownHostException;

public interface ConnectSettings {
    public Connector createConnector() throws IOException;
}
