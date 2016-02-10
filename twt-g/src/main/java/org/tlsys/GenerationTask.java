package org.tlsys;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

import java.util.logging.Logger;

public class GenerationTask extends DefaultTask {

    private static final Logger LOG = Logger.getLogger(GenerationTask.class.getName());

    @TaskAction
    public void samplePluginTasks() throws TaskExecutionException {
        System.out.println("TASK");
        LOG.info("Starting  sample task");
        try {
            TWTPluginExtension extension = getProject().getExtensions().findByType(TWTPluginExtension.class);
            String filePath = extension.getSampleFilePath();
            LOG.info("Sample file path is: " + filePath);
            LOG.info("Successfully completed sample Task");
        }catch(Exception e){
            LOG.info("ERROR");
            e.printStackTrace();
            throw new TaskExecutionException(this,new Exception("Exception occured while processing sampleTask",e));
        }
    }
}
