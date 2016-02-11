package org.tlsys.twt;

import java.io.File;
import java.util.Set;

public abstract class SourceClassLoader extends DClassLoader {
    public SourceClassLoader(DLoader loader) {
        super(loader);
    }

    public abstract Set<File> getSourceFiles();
}
