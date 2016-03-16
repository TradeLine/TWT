package org.tlsys.twt;

import java.util.*;

/**
 * Контекст загруженных зависимостей
 *
 * @author Субочев Антон
 */
public final class DLoader {
    private final Map<String,TWTModule> loaders = new HashMap<>();
    public Optional<TWTModule> getByName(String name) {
        return Optional.ofNullable(loaders.get(name));
    }

    public void add(TWTModule loader) {
        loaders.put(loader.getName(), loader);
    }

    public Collection<TWTModule> getLoaders() {
        return loaders.values();
    }
}
