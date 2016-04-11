package org.tlsys.twt.build;

import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.tlsys.Outbuffer;
import org.tlsys.lex.declare.*;
import org.tlsys.sourcemap.SourceMap;
import org.tlsys.twt.*;

import java.io.*;
import java.util.*;

public class GenerationTask extends TWTPlugin {

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

    /*
    private War war;
    private AbstractCopyTask warIncluds;

    private void includeFile(String f) {
        if (war == null)
            war = (War) getProject().getTasks().stream().filter(e->e instanceof War).findAny().get();
        if (warIncluds == null)
            warIncluds = war.from(getProject().getBuildDir());
        warIncluds.include(f);
        System.out.println("Include " + f);
    }

    private void includeSource(File sourceDir) {
        //war.from(sourceDir).include("*");
        war.from(sourceDir).include("*");
    }
*/
    @TaskAction
    public void samplePluginTasks() throws TaskExecutionException {
        DLoader loader = new DLoader();
        long start = System.currentTimeMillis();
        try {
            AppCompiller.App app = null;
            try {
                long startCompile = System.currentTimeMillis();
                app = AppCompiller.compileApp(this);
                System.out.println("Compile " + (System.currentTimeMillis() - startCompile));
                long startRename = System.currentTimeMillis();
                renaming(app.getMainLoader().getTWTClassLoader());
                System.out.println("Rename " + (System.currentTimeMillis() - startRename));
                for (GenerationTarget gt : getTargets()) {
                    File sourceMap = new File(getProject().getBuildDir(), "sourcemap");
                    File mapFile = new File(sourceMap, gt.out() + ".map");
                    File outFile = new File(getProject().getBuildDir(), gt.out());
                    //AbstractCopyTask warFiles = war.from(outFile.getParent());
                    //includeFile(gt.out().toString());
                    //includeFile(gt.out().toString() + ".map");

                    if (!sourceMap.exists())
                        sourceMap.mkdirs();
                    try (PrintStream ps1 = new PrintStream(new FileOutputStream(outFile), false, "UTF-8")) {
                        Outbuffer ps = new Outbuffer(ps1);
                        CompileModuls cm = new CompileModuls();
                        Optional<VMethod> mainMethod = null;
                        if (gt.main() != null) {
                            VClass mainClass = app.getMainLoader().getTWTClassLoader().loadClass(gt.main(), null);
                            mainMethod = mainClass.getMethodByName("main").stream().filter(e -> e.getParent() == mainClass).findFirst();
                            if (!mainMethod.isPresent()) {
                                throw new CompileException("Can't method main in " + gt.main(), null);
                            }
                            cm.add(mainMethod.get());
                        }

                        for (String c : gt.getClasses()) {
                            cm.add(app.getMainLoader().getTWTClassLoader().loadClass(c, null));
                        }
                        cm.addForced(app.getMainLoader().getTWTClassLoader());
                        //cm.detectReplace();

                        /*
                        for (CompileModuls.ClassRecord cr : cm.getRecords()) {
                            System.out.println("====" + cr.getClazz().getRealName() + "====");
                            for (VExecute e : cr.getExe()) {
                                if (e instanceof VMethod) {
                                    VMethod m = (VMethod)e;
                                    System.out.println(m.getDescription());
                                    Collect col = Collect.create();
                                    m.getUsing(col);
                                    for (CanUse cu : col.get()) {
                                        if (cu instanceof VMethod) {
                                            VMethod mm = (VMethod) cu;
                                            System.out.println("--->" + mm.getParent().getRealName() + "." + mm.getDescription());
                                        }
                                    }
                                }
                            }
                            System.out.println("====" + cr.getClazz().getRealName() + "====\n");
                        }
                        */

                        long startGeneration = System.currentTimeMillis();
                        Class cl = app.getMainLoader().getJavaClassLoader().loadClass(gt.generator());
                        MainGenerator mg = (MainGenerator) cl.newInstance();
                        mg.generate(app.getMainLoader().getTWTClassLoader(), cm, ps);

                        if (mainMethod != null) {
                            mg.generateInvoke(mainMethod.get(), ps);
                        }

                        System.out.println("Generation " + (System.currentTimeMillis() - startGeneration));

                        ps.append("\n//@ sourceMappingURL=" + new File(outFile.getParent()).toURI().relativize(mapFile.toURI()));
                        //ps.getRecords();

                        long startSourceMapGeneration = System.currentTimeMillis();
                        SourceMap sm = new SourceMap(ps.getRecords());
                        try (OutputStream o = new FileOutputStream(mapFile)) {
                            sm.generate(new PrintStream(o));
                            o.flush();
                            o.close();
                        }


                        sm.getFiles().parallelStream().forEach(e -> {
                            File sourceDir = new File(sourceMap, e.getName()).getParentFile();
                            if (!sourceDir.exists())
                                sourceDir.mkdirs();
                            File f = new File(sourceMap, e.getName());
                            try (OutputStream o = new FileOutputStream(f)) {
                                o.write(e.getData().getBytes());
                                o.flush();
                                o.close();
                            } catch (FileNotFoundException e1) {
                                throw new RuntimeException(e1);
                            } catch (IOException e1) {
                                throw new RuntimeException(e1);
                            }
                        });
                        System.out.println("SourceMap " + (System.currentTimeMillis() - startSourceMapGeneration));

                        //includeSource(sourceMap);



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
