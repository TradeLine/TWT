package org.tlsys.twt.compile;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.tlsys.twt.AppCompiller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashSet;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFiles;
import org.tlsys.twt.TWTModule;
import org.tlsys.twt.compiler.ModuleInfo;

public class CompileTask extends DefaultTask {

    public static File getOutpit(Project project) {
        return new File(AppCompiller.getClassDir(project) + File.separator + TWTModule.FILE);
    }
/*
    private static File getOutModuleInfo(Project project) {
        return new File(AppCompiller.getClassDir(project) + File.separator + ModuleInfo.FILE);
    }
*/
    
    @OutputFiles
    public Collection<File> getOut() {
        HashSet<File> out = new HashSet<>();
        out.add(getOutpit(getProject()));
        //out.add(getOutModuleInfo(getProject()));
        return out;
    }

    @InputFiles
    public Collection<File> getIn() {
        return AppCompiller.getSourceOfProject(getProject());
    }

    @TaskAction
    public void samplePluginTasks() throws TaskExecutionException {

        AppCompiller.App app = null;
        try {
            AppCompiller.getSourceOfProject(getProject());
            try {
                app = AppCompiller.compileApp(this);

                File outFile = getOutpit(getProject());

                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    app.getMainLoader().saveJSClassLoader(fos);
                }
                //app.getInfo().saveTo(AppCompiller.getClassDir(getProject()));
            } finally {
                if (app != null) {
                    app.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new TaskExecutionException(this, new Exception("Exception occured while processing sampleTask", e));
        }

    }
}
