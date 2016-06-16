package org.tlsys.classpath;

import java.io.InputStream;

/**
 * Created by Субочев Антон on 16.06.2016.
 */
public class StreamClasspathFile implements ClasspathFile {
    private final InputStream stream;
    private final String name;

    public StreamClasspathFile(InputStream stream, String name) {
        this.stream = stream;
        this.name = name;
    }

    @Override
    public String getFilename() {
        return name;
    }

    @Override
    public InputStream openInputStream() {
        return stream;
    }

    @Override
    public long getLastModified() {
        return 0;
    }

    @Override
    public void close() {
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public long getCRC() {
        return 0;
    }
}
