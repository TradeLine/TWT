package org.tlsys.twt.compile;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.tlsys.twt.AppCompiller;
import org.tlsys.twt.DClassLoader;

import java.io.File;
import java.io.FileOutputStream;

public class CompileTask extends DefaultTask {

    public static File getOutpit(Project project) {
        return new File(AppCompiller.getClassDir(project) + File.separator + DClassLoader.JSLIB);
    }



    @OutputFile
    public File getOut() {
        return getOutpit(getProject());
    }

    @TaskAction
    public void samplePluginTasks() throws TaskExecutionException {
        AppCompiller.App app = null;
        try {
            try {
                app = AppCompiller.compileApp(this);

                File outFile = getOutpit(getProject());

                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    app.getMainLoader().saveJSClassLoader(fos);
                }
            } finally {
                if (app != null)
                    app.close();
            }

        }catch(Exception e){
            e.printStackTrace();
            throw new TaskExecutionException(this,new Exception("Exception occured while processing sampleTask",e));
        }

    }
}
