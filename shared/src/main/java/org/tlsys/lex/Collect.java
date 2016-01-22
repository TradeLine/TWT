package org.tlsys.lex;

import java.util.Collection;
import java.util.HashSet;

public final class Collect {
    private final HashSet<CanUse> using = new HashSet<>();

    private Collect() {
    }

    public static Collect create() {
        return new Collect();
    }

    public Collect add(Using... o) {
        for (Using e : o) {
            if (e == null)
                continue;
            if (using.contains(e))
                continue;
            if (e instanceof CanUse)
                using.add((CanUse) e);
            add(e.getUsing());
        }
        return this;
    }

    public Collect add(Collect... e) {
        for (Collect o : e) {
            if (o == null)
                continue;
            using.addAll(o.get());
        }

        return this;
    }

    public HashSet<CanUse> get() {
        return using;
    }
}
