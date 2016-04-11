package org.tlsys;

import java.io.InputStream;
import java.util.Optional;

public interface FileProvider {
    public Optional<InputStream> getFile(String name);
}
