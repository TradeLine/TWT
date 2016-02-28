/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tlsys.twt;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.tlsys.lex.declare.VClassLoader;
import org.tlsys.twt.compiler.SourceFinder;

/**
 *
 * @author subochev
 */
public abstract class TWTModuleSource extends TWTModuleLoader {

    public static Set<File> findSource(File classes, File source) throws IOException {
        return SourceFinder.getCompileClasses(classes, name -> {
            String classFilePath = name.replace('.', File.separatorChar) + ".java";
            File f = new File(source + File.separator + classFilePath);
            if (f.isFile()) {
                return Optional.of(f);
            }
            return Optional.empty();
        });
    }

    private final Set<File> sourcees;

    public TWTModuleSource(File classses, File sources) throws IOException {
        super(classses);

        sourcees = findSource(classses, sources);
    }

    public Set<File> getSourcees() {
        return sourcees;
    }

    @Override
    public Collection<TWTModule> getParents() {
        return parents;
    }

    public void setTWTClassLoader(VClassLoader loader) {
        twtClassLoader = loader;
        noTWT = false;

        for (TWTModule c : parents) {
            VClassLoader l = c.getTWTClassLoader();
            if (l != null) {
                loader.parents.add(l);
            }
        }
    }

    public void saveJSClassLoader(OutputStream outputStream) throws IOException {
        Objects.requireNonNull(twtClassLoader, "JS loader os NULL");
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(twtClassLoader);
    }

}
