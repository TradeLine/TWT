package org.tlsys.twt;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

public class JarDClassLoader extends DClassLoader {

    private final String name;
    private final File file;

    public File getFile() {
        return file;
    }

    public JarDClassLoader(DLoader loader, File jarFile, String name) {
        super(loader);
        this.name = name;
        this.file = jarFile;
        if (!file.isFile()) {
            throw new RuntimeException("Jar file " + jarFile + " not found");
        }

        if (!file.getName().endsWith(".jar")) {
            throw new RuntimeException("File " + jarFile + " is not JAR");
        }

        try {
            addURL(jarFile.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private final Set<DClassLoader> parents = new HashSet<>();

    @Override
    public Set<DClassLoader> getParents() {
        return parents;
    }

    @Override
    public String getName() {
        return name;
    }
}
