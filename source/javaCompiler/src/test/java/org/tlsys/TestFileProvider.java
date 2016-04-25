package org.tlsys;

import java.io.InputStream;
import java.util.Optional;

public class TestFileProvider implements FileProvider {
    @Override
    public Optional<InputStream> getFile(String name) {
        return null;
    }
}
