package org.tlsys.wildfly;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public interface Connector {
    public DeployManager getDeployManager();
    public byte[] uploadContent(byte[] data) throws IOException;
    public default byte[] uploadContent(InputStream stream) throws IOException {
        byte[]buffer = new byte[512];
        int len = 0;
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        while ((len = stream.read(buffer)) > 0) {
            bo.write(buffer, 0, len);
        }
        return uploadContent(bo.toByteArray());
    }
    public State getState() throws IOException;

    public enum State {
        NOT_RUN, STARTING, RUNNING, RESTART_REQUIRED
    }
}
