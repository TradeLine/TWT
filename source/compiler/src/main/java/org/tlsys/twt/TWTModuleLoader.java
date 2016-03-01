/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tlsys.twt;

import org.tlsys.lex.declare.VClassLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author subochev
 */
public abstract class TWTModuleLoader implements TWTModule {

    private final TWTClassLoader loader;
    
    protected final HashSet<TWTModule> parents = new HashSet<>();
    
    public void addParent(TWTModule module) {
        parents.add(module);
        
        getJavaClassLoader().parents.add(module.getJavaClassLoader());
        if (twtClassLoader != null && module.getTWTClassLoader() != null)
            getTWTClassLoader().parents.add(module.getTWTClassLoader());
    }

    @Override
    public Collection<TWTModule> getParents() {
        return parents;
    }
    
    

    public TWTModuleLoader(File file) throws IOException {
        if (file.isFile()) {
            loader = new JarClassLoader(file, this);
        } else {
            if (file.isDirectory()) {
                loader = new DirectoryClassLoader(file, this);
            } else {
                throw new IOException("Can't load " + file + ": unknown file");
            }
        }
    }

    protected VClassLoader twtClassLoader;

    @Override
    public TWTClassLoader getJavaClassLoader() {
        return loader;
    }
    
    protected boolean noTWT = false;

    @Override
    public VClassLoader getTWTClassLoader() {
        if (noTWT)
            return null;
        if (twtClassLoader != null)
            return twtClassLoader;
        try (InputStream is = getJavaClassLoader().getResourceAsStream(TWTModule.FILE)) {
            if (is == null) {
                noTWT = true;
                return null;
            }
            
            ArrayList<VClassLoader> cl = new ArrayList<>(parents.size());
            for (TWTModule p : parents) {
                VClassLoader vv = p.getTWTClassLoader();
                if (vv != null)
                    cl.add(vv);
            }
            VClassLoader.setParentList(cl);
            ObjectInputStream ois = new ObjectInputStream(is);
            twtClassLoader = (VClassLoader) ois.readObject();
            twtClassLoader.setJavaClassLoader(getJavaClassLoader());

            return twtClassLoader;
        } catch (IOException e) {
            throw new RuntimeException(getName() + " Parents="+parents, e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(getName(), e);
        }
    }

}
