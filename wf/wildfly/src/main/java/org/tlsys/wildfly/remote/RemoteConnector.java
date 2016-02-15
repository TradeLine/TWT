package org.tlsys.wildfly.remote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.tlsys.wildfly.Connector;
import org.tlsys.wildfly.exceptions.*;

public class RemoteConnector implements Connector {
    private String login;
    private String password;
    private ModelControllerClient client;
    private RemoteDeployManager deployManager;

    static final String OPERATION = "operation";



    public ModelControllerClient getClient() {
        return client;
    }

    public synchronized RemoteDeployManager getDeployManager() {
        if (deployManager == null)
            deployManager = new RemoteDeployManager(this);
        return deployManager;
    }



    private final CallbackHandler callbackHandler = new CallbackHandler() {

        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (Callback current : callbacks) {
                if (current instanceof NameCallback) {
                    NameCallback ncb = (NameCallback) current;
                    ncb.setName(login);
                } else if (current instanceof PasswordCallback) {
                    PasswordCallback pcb = (PasswordCallback) current;
                    pcb.setPassword(password.toCharArray());
                } else if (current instanceof RealmCallback) {
                    RealmCallback rcb = (RealmCallback) current;
                    rcb.setText(rcb.getDefaultText());
                } else {
                    throw new UnsupportedCallbackException(current);
                }
            }
        }
    };
    /*
    public void getAllJDBCDrivers() throws IOException{
        ModelNode op = new ModelNode();
        op.get("operation").set("read-children-resources(name=datasources)");
        //op.get("child-type").set("subsystem=datasources/jdbc-driver");
        op.get("address").add("subsystem");
        op.get("child-type").set("datasources");
        LOG.info("IN\n" + op.toJSONString(false));
        ModelNode out = client.execute(op);
        LOG.info("OUT\n" + out.toJSONString(false));
    }
    */
    public RemoteConnector(String login, String password, String host, int port) throws UnknownHostException {
        this.login = login;
        this.password = password;
        client = ModelControllerClient.Factory.create(InetAddress.getByName(host), port, callbackHandler);
    }



    public void remove(String fileName) throws IOException, WildFlyException {
        ModelNode op = new ModelNode();
        op.get(OPERATION).set("remove");
        ModelNode addr = op.get("address");
        addr.add("deployment", fileName);
        ModelNode out = client.execute(op);
        String outcome = out.get("outcome").asString();
        if (!outcome.equals("success")) {
            String message = out.get("failure-description").asString();
            throw new WildFlyException(message);
        }
    }

    @Override
    public State getState() throws IOException {
        try {
            ModelNode op = new ModelNode();
            op.get(OPERATION).set("read-attribute");
            op.get("name").set("server-state");
            ModelNode out = client.execute(op);
            String outcome = out.get("outcome").asString();
            if (!outcome.equals("success")) {
                String message = out.get("failure-description").asString();
                throw new RuntimeException(new WildFlyException(message));
            }
            String result = out.get("result").asString().toUpperCase();
            if (result.equals("STARTING"))
                return State.STARTING;
            if (result.equals("RUNNING"))
                return State.RUNNING;
            if (result.equals("RESTART_REQUIRED"))
                return State.RESTART_REQUIRED;
            throw new RuntimeException(new WildFlyException("Unknown state type " + result));
        } catch (java.net.ConnectException e) {
            return State.NOT_RUN;
        }
    }

    @Override
    public byte[] uploadContent(byte[] data) throws IOException {
        ModelNode op = new ModelNode();
        op.get(OPERATION).set("upload-deployment-bytes");
        op.get("bytes").set(data);
        ModelNode out = client.execute(op);
        String outcome = out.get("outcome").asString();
        if (!outcome.equals("success")) {
            String message = out.get("failure-description").asString();
            throw new RuntimeException(new WildFlyException(message));
        }
        byte[] hash = out.get("result").asBytes();
        return hash;
    }



    public void close() throws IOException {
        client.close();
    }
}
