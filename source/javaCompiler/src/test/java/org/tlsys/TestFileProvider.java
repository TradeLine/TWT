package org.tlsys;

import org.tlsys.twt.SourceProvider;

import java.io.InputStream;
import java.util.Optional;

public class TestFileProvider implements SourceProvider {
    @Override
    public Optional<InputStream> getFile(String name) {
        return null;
    }
}
