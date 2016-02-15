package org.tlsys.wildfly.remote;

import org.tlsys.wildfly.Connector;
import org.tlsys.wildfly.DeployManager;
import org.tlsys.wildfly.Resurce;
import org.tlsys.wildfly.exceptions.ResourceNotFoundException;
import org.tlsys.wildfly.exceptions.*;

import java.io.IOException;
import org.jboss.dmr.*;
import org.tlsys.wildfly.remote.RemoteConnector;

public class RemoteDeployManager implements DeployManager {
    private RemoteConnector control;

    public RemoteDeployManager(RemoteConnector control) {
        this.control = control;
    }

    public RemoteConnector getControl() {
        return control;
    }



    @Override
    public void undeploy(String fileName) throws IOException, WildFlyException {
        ModelNode op = new ModelNode();
        op.get("operation").set("undeploy");
        ModelNode addr = op.get("address");
        addr.add("deployment", fileName);
        ModelNode out = getControl().getClient().execute(op);
        String outcome = out.get("outcome").asString();
        if (!outcome.equals("success")) {
            String message = out.get("failure-description").asString();
            if (message.contains("Management resource") && message.endsWith("not found"))
                throw new ResourceNotFoundException(message);
            throw new WildFlyException(message);
        }
    }

    @Override
    public Resurce[] getList() throws IOException, WildFlyException {
        ModelNode op = new ModelNode();
        op.get("operation").set("read-children-resources");
        op.get("child-type").set("deployment");
        ModelNode res = getControl().getClient().execute(op);
        String outcome = res.get("outcome").asString();
        if (!outcome.equals("success")) {
            String message = res.get("failure-description").asString();
            throw new WildFlyException(message);
        }
        ModelNode result = res.get("result");
        Resurce[]list = new Resurce[result.keys().size()];
        int i = 0;
        for (String k : result.keys()) {
            ModelNode node = result.get(k);
            list[i] = new Resurce(node.get("name").asString(), node.get("content").get(0).get("hash").asBytes(), node.get("enabled").asBoolean(), node.get("runtime-name").asString());
            i++;
        }
        return list;
    }

    @Override
    public Connector getConnector() {
        return control;
    }

    @Override
    public void deploy(String fileName, byte[] data) throws IOException, WildFlyException {
        ModelNode op = new ModelNode();
        op.get("content").add("hash", data);


        op.get("operation").set("add");
        op.get("enabled").set("true");
        op.get("address").add("deployment", fileName);

        ModelNode out = getControl().getClient().execute(op);
        String outcome = out.get("outcome").asString();
        if (outcome.equals("success"))
            return;
        if (outcome.equals("failed")) {
            String message = out.get("failure-description").asString();
            if (message.contains("Duplicate resource"))
                throw new DuplicateResourceException(message);
            if (message.contains("No deployment content"))
                throw new NoDeployContentException(message);

            throw new WildFlyException(message);
        }
    }
}
