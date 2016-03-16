/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tlsys.twt.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author subochev
 */
public class ModuleInfo implements Serializable {
    
    public static final String FILE = "twt_module.data";
    
    private final String name;
    private final String[] parents;

    public ModuleInfo(String name, Collection<String> parents) {
        this(name, parents.stream().toArray(String[]::new));
    }
    
    public ModuleInfo(String name, String[] parents) {
        this.name = name;
        this.parents = parents;
    }

    public String getName() {
        return name;
    }

    public String[] getParents() {
        return parents;
    }
    
    public void saveTo(File name) throws IOException {
        if (!name.isDirectory())
            throw new IllegalArgumentException("Argument name must be directory! name=" + name);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(name.getAbsolutePath()+File.separator+FILE)))) {
            out.writeObject(this);
        }
    }
    
    public static Optional<ModuleInfo> loadFrom(File file) throws IOException {
        URL path = null;
        if (file.isFile()) {
            if (!file.getName().toLowerCase().endsWith(".jar"))
                throw new IllegalArgumentException("Argument file must be JAR file! file=" + file);
            path = new URL("jar:" + file.toURI().toURL() + "!/" + FILE);
        }
        if (file.isDirectory()) {
            path = new File(file + File.separator + FILE).toURI().toURL();
        }
        
        if (path == null)
            throw new RuntimeException("Can't load ModuleInfo from " + file);
        
        try (ObjectInputStream in = new ObjectInputStream(path.openStream())) {
            try {
                return Optional.of((ModuleInfo) in.readObject());
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        } catch (FileNotFoundException e) {
            return Optional.empty();
        }
    }
}
