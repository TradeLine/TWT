package org.tlsys.twt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;

public class FSSourceProvider implements SourceProvider {
    private final File root;

    public FSSourceProvider(File root) {
        this.root = root;
    }

    @Override
    public Optional<InputStream> getFile(String name) {
        try {
            return Optional.of(new FileInputStream(new File(root, name)));
        } catch (FileNotFoundException e) {
            return Optional.empty();
        }
    }
}
