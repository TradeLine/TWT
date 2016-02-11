package org.tlsys.twt;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Контекст загруженных зависимостей
 *
 * @author Субочев Антон
 */
public final class DLoader {
    private final Map<String,DClassLoader> loaders = new HashMap<String, DClassLoader>();
    public Optional<DClassLoader> getByName(String name) {
        return Optional.ofNullable(loaders.get(name));
    }

    public void add(DClassLoader loader) {
        loaders.put(loader.getName(), loader);
    }
}
