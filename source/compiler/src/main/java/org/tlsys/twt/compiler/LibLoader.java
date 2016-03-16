/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tlsys.twt.compiler;

/**
 *
 * @author subochev
 */
public class LibLoader {
/*
    private static class ModuleRecord {

        private final ModuleInfo info;
        private final File path;
        public final Set<ModuleRecord> parents = new HashSet<>();

        public ModuleRecord(ModuleInfo info, File path) {
            this.info = info;
            this.path = path;
        }

        public ModuleInfo getInfo() {
            return info;
        }

        public File getPath() {
            return path;
        }

        private DClassLoader loader = null;

        public DClassLoader getClassLoader() {
            if (loader == null) {
                if (getPath().isFile()) {
                    if (!getPath().getName().toLowerCase().endsWith(".jar")) {
                        throw new RuntimeException("Path is not jar! " + getPath());
                    }
                }
                loader = new PathDClassLoader(getPath(), getInfo()==null?getPath().getAbsolutePath():getInfo().getName());
                for (ModuleRecord mr : parents) {
                    loader.getParents().add(mr.getClassLoader());
                }
            }

            return loader;
        }
    }

    public static Set<String> getAllDependencys(DClassLoader loader) {
        HashSet<String> out = new HashSet<>();
        for (DClassLoader l : loader.getParents()) {
            if (l.getJsClassLoader() == null) {
                continue;
            }
            out.add(l.getName());
            out.addAll(getAllDependencys(l));
        }
        return out;
    }

    public static void loadLibsFor(DClassLoader loader, Collection<File> classPath) throws IOException {
        System.out.println("Search libs for " + loader.getName() + "... PATH=" + classPath);
        Set<ModuleRecord> recs = new HashSet<>();
        FILES:for (File f : classPath) {
            Optional<ModuleInfo> oo = ModuleInfo.loadFrom(f);
            for (ModuleRecord mr : recs) {
                if (mr.getInfo() != null && oo.isPresent() && oo.get().getName().equals(mr.getInfo().getName()))
                    continue FILES;
                if (mr.getInfo()==null && !oo.isPresent() && mr.getPath().equals(f))
                    continue FILES;
            }
            System.out.println(f+"  LIBS=" + oo.isPresent() + (oo.isPresent()?" NAME=" + oo.get().getName():""));
            recs.add(new ModuleRecord(oo.orElse(null), f));
        }

        System.out.println("Set links...");
        for (ModuleRecord mr : recs) {
            if (mr.getInfo() != null) {
                DEPS:
                for (String d : mr.getInfo().getParents()) {
                    for (ModuleRecord mrd : recs) {
                        if (mrd == mr) {
                            continue;
                        }
                        if (mr.getInfo() != null && mr.getInfo().getName().equals(d)) {
                            mr.parents.add(mrd);
                            continue DEPS;
                        } else {
                            mr.parents.add(mrd);
                        }
                    }
                    throw new RuntimeException("Can't find dependency " + d + " for " + mr.getInfo().getName());
                }
            }
        }
        System.out.println("READY FOR APPEND " + recs.size());
        for (ModuleRecord mr : recs) {
            loader.getParents().add(mr.getClassLoader());
            if (mr.getClassLoader().getJsClassLoader() != null)
                loader.getJsClassLoader().parents.add(mr.getClassLoader().getJsClassLoader());
        }
    }
    */
}
