package org.tlsys.wildfly;

import org.tlsys.wildfly.exceptions.WildFlyException;

import java.io.IOException;
import java.io.InputStream;

public interface DeployManager {
    public void undeploy(String fileName) throws IOException, WildFlyException;
    public void remove(String appName) throws IOException, WildFlyException;
    public void deploy(String fileName, byte[] hash) throws IOException, WildFlyException;
    public default void deploy(String fileName, InputStream input) throws IOException, WildFlyException {
        byte[] hash = getConnector().uploadContent(input);
        deploy(fileName, hash);
    }
    public Resurce[] getList() throws IOException, WildFlyException;
    public Connector getConnector();
}
