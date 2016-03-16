package org.tlsys.wildfly;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

import java.io.FileInputStream;

public class DeployTask extends DefaultTask {

    @TaskAction
    public void deploy() throws TaskExecutionException {
        try {
            WildflySettings extension = getProject().getExtensions().findByType(WildflySettings.class);
            Connector connector = extension.getConnector().createConnector();
            try (FileInputStream fin = new FileInputStream(extension.file())){
                connector.getDeployManager().deploy(extension.name(), fin);
            }
        } catch (Throwable e) {
            throw new TaskExecutionException(this, e);
        }
    }
}
