package org.tlsys.twt.build;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.tlsys.Outbuffer;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

public class GenerationTask extends DefaultTask {

    private static long classNameIterator = 0;
    private static long methodNameIterator = 0;
    private static int fieldIterator = 0;
    private ArrayList<GenerationTarget> targets = new ArrayList<>();

    public static void renaming(VClassLoader loader) {
        String name = loader.getName();
        loader.setName(name.replace('-', '_').replace('.', '$'));

        for (VClass v : loader.classes) {
            if (v.alias == null) {
                v.alias = v.fullName;
            }
            v.fullName = v.fullName.replace('.', '_') + "_" + Long.toString(++classNameIterator, Character.MAX_RADIX);

            for (VField f : v.getLocalFields()) {
                f.setRuntimeName(f.getRealName() + Integer.toString(++fieldIterator, Character.MAX_RADIX));
            }

            for (VMethod m : v.methods) {
                if (m.alias == null) {
                    m.alias = m.getRunTimeName();
                }
                int argIterator = 0;
                for (VArgument a : m.getArguments()) {
                    a.setRuntimeName(a.getRealName() + "_" + Integer.toString(++argIterator, Character.MAX_RADIX));
                }
                if (m.getReplace() == null) {
                    m.setRuntimeName(m.getRunTimeName() + "_" + Long.toString(++methodNameIterator, Character.MAX_RADIX));
                }
            }

            int constructIterator = 0;

            for (VConstructor m : v.constructors) {
                m.setRuntimeName("c" + Integer.toString(++constructIterator, Character.MAX_RADIX));
                int argIterator = 0;
                for (VArgument a : m.getArguments()) {
                    a.setRuntimeName(a.getRealName() + "_" + Integer.toString(++argIterator, Character.MAX_RADIX));
                }
            }
        }

        for (VClassLoader cl : loader.parents) {
            renaming(cl);
        }
    }

    public void target(GenerationTarget target) {
        targets.add(target);
    }

    public GenerationTarget target() {
        GenerationTarget gr = new GenerationTarget();
        target(gr);
        return gr;
    }

    public ArrayList<GenerationTarget> getTargets() {
        return targets;
    }

    @OutputFiles
    public Set<File> getOut() {
        HashSet<File> outs = new HashSet<>();
        for (GenerationTarget gt : getTargets()) {
            File outFile = new File(getProject().getBuildDir(), gt.out());
            outs.add(outFile);
        }

        return outs;
    }

    @TaskAction
    public void samplePluginTasks() throws TaskExecutionException {
        DLoader loader = new DLoader();
        try {
            AppCompiller.App app = null;
            try {
                app = AppCompiller.compileApp(this);
                renaming(app.getMainLoader().getTWTClassLoader());
                for (GenerationTarget gt : getTargets()) {
                    File outFile = new File(getProject().getBuildDir(), gt.out());
                    try (PrintStream ps1 = new PrintStream(new FileOutputStream(outFile), false, "UTF-8")) {
                        Outbuffer ps = new Outbuffer(ps1);
                        CompileModuls cm = new CompileModuls();
                        Optional<VMethod> mainMethod = null;
                        if (gt.main() != null) {
                            VClass mainClass = app.getMainLoader().getTWTClassLoader().loadClass(gt.main());
                            mainMethod = mainClass.getMethodByName("main").stream().filter(e -> e.getParent() == mainClass).findFirst();
                            if (!mainMethod.isPresent()) {
                                throw new CompileException("Can't method main in " + gt.main());
                            }
                            cm.add(mainMethod.get());
                        }

                        for (String c : gt.getClasses()) {
                            cm.add(app.getMainLoader().getTWTClassLoader().loadClass(c));
                        }
                        cm.addForced(app.getMainLoader().getTWTClassLoader());
                        cm.detectReplace();

                        /*
                        for (CompileModuls.ClassRecord cr : cm.getRecords()) {
                            for (VExecute e : cr.getExe()) {
                                if (e instanceof VMethod) {
                                    VMethod m = (VMethod)e;
                                }
                            }
                        }
                        */

                        Class cl = app.getMainLoader().getJavaClassLoader().loadClass(gt.generator());
                        MainGenerator mg = (MainGenerator) cl.newInstance();
                        mg.generate(app.getMainLoader().getTWTClassLoader(), cm, ps);

                        if (mainMethod != null) {
                            mg.generateInvoke(mainMethod.get(), ps);
                        }
                    }
                }
            } finally {
                if (app != null) {
                    app.close();
                }
            }
            /*
            try (FileOutputStream fos = new FileOutputStream(new File(classDir+File.separator+ DClassLoader.JSLIB))) {
                mainLoader.saveJSClassLoader(fos);
            }
             */
        } catch (Exception e) {
            e.printStackTrace();
            throw new TaskExecutionException(this, new Exception("Exception occured while processing sampleTask", e));
        }
    }

    @InputFiles
    public Collection<File> getIn() {
        return AppCompiller.getSourceOfProject(getProject());
    }
}
