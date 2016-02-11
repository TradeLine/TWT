package org.tlsys.lex;

import org.tlsys.lex.declare.ArrayClass;

import java.util.Collection;
import java.util.HashSet;

public final class Collect {
    private final HashSet<CanUse> using = new HashSet<>();
    private final HashSet<Object> skeep = new HashSet<>();

    private Collect() {
    }

    public static Collect create() {
        return new Collect();
    }

    public Collect add(Using... o) {
        for (Using e : o) {
            if (e == null)
                continue;
            if (using.contains(e) || skeep.contains(e))
                continue;
            if (e instanceof CanUse && !(e instanceof ArrayClass))
                using.add((CanUse) e);
            else
                skeep.add(e);
            e.getUsing(this);
        }
        return this;
    }
/*
    public Collect add(Collect... e) {
        for (Collect o : e) {
            if (o == null)
                continue;
            using.addAll(o.get());
            skeep.addAll(o.skeep);
        }

        return this;
    }
*/
    public HashSet<CanUse> get() {
        return using;
    }
}
