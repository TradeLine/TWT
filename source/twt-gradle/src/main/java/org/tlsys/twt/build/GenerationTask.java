package org.tlsys.twt.build;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GenerationTask extends DefaultTask {

    private ArrayList<GenerationTarget> targets = new ArrayList<>();

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
        System.out.println("==============HELLO==========");
        DLoader loader = new DLoader();

        try {
            AppCompiller.App app = null;
            try {
                app = AppCompiller.compileApp(this);
                renaming(app.getMainLoader().getJsClassLoader());
                for (GenerationTarget gt : getTargets()) {
                    File outFile = new File(getProject().getBuildDir(), gt.out());
                    try (PrintStream ps = new PrintStream(new FileOutputStream(outFile))) {
                        CompileModuls cm = new CompileModuls();
                        Optional<VMethod> mainMethod = null;
                        if (gt.main() != null) {
                            VClass mainClass = app.getMainLoader().getJsClassLoader().loadClass(gt.main());
                            mainMethod = mainClass.getMethodByName("main").stream().filter(e -> e.getParent() == mainClass).findFirst();
                            if (!mainMethod.isPresent())
                                throw new CompileException("Can't method main in " + gt.main());
                            cm.add(mainMethod.get());
                        }

                        for (String c : gt.getClasses()) {
                            cm.add(app.getMainLoader().getJsClassLoader().loadClass(c));
                        }
                        cm.detectReplace();

                        System.out.println("Searching generator..." + gt.generator());
                        Class cl = app.getMainLoader().loadClass(gt.generator());
                        MainGenerator mg = (MainGenerator) cl.newInstance();
                        mg.generate(app.getMainLoader().getJsClassLoader(), cm, ps);

                        if (mainMethod != null) {
                            mg.generateInvoke(mainMethod.get(), ps);
                        }
                    }
                }
            } finally {
                if (app != null)
                    app.close();
            }
            /*
            try (FileOutputStream fos = new FileOutputStream(new File(classDir+File.separator+ DClassLoader.JSLIB))) {
                mainLoader.saveJSClassLoader(fos);
            }
            */
        }catch(Exception e){
            e.printStackTrace();
            throw new TaskExecutionException(this,new Exception("Exception occured while processing sampleTask",e));
        } finally {
            for(DClassLoader cl : loader.getLoaders()) {
                try {
                    cl.close();
                } catch (IOException io) {
                    //ignore
                }
            }
        }
    }

    private static long classNameIterator = 0;
    private static long methodNameIterator = 0;
    private static int fieldIterator = 0;

    public static void renaming(VClassLoader loader) {
        String name = loader.getName();
        loader.setName(name.replace('-', '_').replace('.', '$'));

        for (VClass v : loader.classes) {
            if (v.alias == null)
                v.alias = v.fullName;
            v.fullName = v.fullName.replace('.','_')+"_" + Long.toString(++classNameIterator, Character.MAX_RADIX);


            for (VField f : v.fields) {
                if (f.alias == null)
                    f.alias = f.name;
                f.name = "f" + Integer.toString(++fieldIterator, Character.MAX_RADIX);
            }

            for (VMethod m : v.methods) {
                if (m.alias == null)
                    m.alias = m.getRunTimeName();
                int argIterator = 0;
                for (VArgument a : m.arguments) {
                    a.name = a.name+"_" + Integer.toString(++argIterator, Character.MAX_RADIX);
                }
                if (m.getReplace() == null)
                    m.setRuntimeName(m.getRunTimeName()+"_" + Long.toString(++methodNameIterator, Character.MAX_RADIX));
            }

            int constructIterator = 0;

            for (VConstructor m : v.constructors) {
                m.setRuntimeName("c" + Integer.toString(++constructIterator, Character.MAX_RADIX));
                int argIterator = 0;
                for (VArgument a : m.arguments) {
                    a.name = "a" + Integer.toString(++argIterator, Character.MAX_RADIX);
                }
            }
        }

        for (VClassLoader cl : loader.parents)
            renaming(cl);
    }
}
