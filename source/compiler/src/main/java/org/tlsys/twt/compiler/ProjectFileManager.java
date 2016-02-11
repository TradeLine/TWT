package org.tlsys.twt.compiler;

import javax.tools.*;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class ProjectFileManager implements JavaFileManager {
    private final StandardJavaFileManager standardFileManager;
    private final PackageInternalsFinder finder;

    public ProjectFileManager(StandardJavaFileManager standardFileManager, ClassLoader buildClassLoader) {
        this.standardFileManager = standardFileManager;
        this.finder = new PackageInternalsFinder(buildClassLoader);
    }

    public StandardJavaFileManager getStandardFileManager() {
        return standardFileManager;
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return null;
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        if (location == StandardLocation.PLATFORM_CLASS_PATH) { // let standard manager hanfle
            return standardFileManager.list(location, packageName, kinds, recurse);
        } else if (location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) {
            if (packageName.startsWith("java.")) { // a hack to let standard manager handle locations like "java.lang"
                // or
                // "java.util". Prob would make sense to join results of standard
                // manager with those of my finder here
                return standardFileManager.list(location, packageName, kinds, recurse);
            } else { // app specific classes are here
                return finder.find(packageName, recurse);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (file instanceof CustomJavaFileObject) {
            return ((CustomJavaFileObject) file).binaryName();
        } else { // if it's not CustomJavaFileObject, then it's coming from standard file manager - let it handle the
            // file
            return standardFileManager.inferBinaryName(location, file);
        }
    }

    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
        return false;
    }

    @Override
    public boolean handleOption(String current, Iterator<String> remaining) {
        return false;
    }

    @Override
    public boolean hasLocation(Location location) {
        return false;
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
        return null;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        return null;
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
        return null;
    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
        return null;
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public int isSupportedOption(String option) {
        return 0;
    }
}
