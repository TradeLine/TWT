/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tlsys.twt.module;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.tlsys.twt.AppCompiller;
import org.tlsys.twt.compiler.ModuleInfo;

/**
 *
 * @author subochev
 */
public class ModuleTask extends DefaultTask {

    @OutputFile
    public File getOut() {
        return new File(AppCompiller.getClassDir(getProject()) + File.separator + ModuleInfo.FILE);
    }

    @TaskAction
    public void samplePluginTasks() throws TaskExecutionException {
        ModuleInfo mi = new ModuleInfo(getProject().getGroup() + "-" + getProject().getName(), new String[]{});
        try {
            mi.saveTo(AppCompiller.getClassDir(getProject()));
        } catch (IOException ex) {
            throw new TaskExecutionException(this, ex);
        }
    }
}
