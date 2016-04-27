package org.tlsys;

import org.tlsys.twt.members.TClassLoader;
import org.tlsys.twt.members.VClass;

import java.util.HashMap;
import java.util.Optional;

public class CompileClassLoader extends TClassLoader {
    private JavaSourceSet javaSourceSet;

    private HashMap<String, String> alias = new HashMap<>();

    public CompileClassLoader() {
    }

    public JavaSourceSet getJavaSourceSet() {
        return javaSourceSet;
    }

    public void setJavaSourceSet(JavaSourceSet javaSourceSet) {
        this.javaSourceSet = javaSourceSet;
    }

    public void addAlias(String alias, String realName) {
        this.alias.put(alias, realName);
    }

    @Override
    public Optional<VClass> findClassByName(String name) {
        String real = alias.get(name);
        if (real != null)
            name = real;

        Optional<VClass> ck = super.findClassByName(name);
        if (ck.isPresent())
            return ck;

        /*
        if (!name.contains(".")) {
            Optional<VClass> o = findClassByName("org.tlsys.T" + name);
            if (o.isPresent())
                return o;
        }
        */

        ck = super.findClassByName(name);
        if (ck.isPresent())
            return ck;


        return javaSourceSet.getClass(name);
    }
}
