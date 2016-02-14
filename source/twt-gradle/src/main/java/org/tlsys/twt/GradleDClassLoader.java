package org.tlsys.twt;

import org.tlsys.twt.DClassLoader;
import org.tlsys.twt.DLoader;

import java.util.Set;

public abstract class GradleDClassLoader extends DClassLoader {

    private final String name;

    public GradleDClassLoader(String name, DLoader loader) {
        super(loader);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
