package org.tlsys.twt;

import java.io.InputStream;
import java.util.Optional;

public interface SourceProvider {
    public Optional<InputStream> getFile(String name);
}
