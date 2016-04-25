package org.tlsys.twt.members;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TClassLoader {

    private final List<VClass> classes = new ArrayList<>();

    public void addClass(VClass clazz) {
        classes.add(clazz);
    }

    public Optional<VClass> findClassByName(String name) {
        for (VClass cl : classes) {
            if (cl.getName().equals(name))
                return Optional.of(cl);
            if (cl.getRealTimeName().equals(name))
                return Optional.of(cl);
        }
        return Optional.empty();
    }
}
