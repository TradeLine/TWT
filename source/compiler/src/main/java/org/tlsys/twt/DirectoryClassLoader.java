/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tlsys.twt;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author subochev
 */
public class DirectoryClassLoader extends TWTClassLoader {

    private final File root;
    private final TWTModule module;

    public DirectoryClassLoader(File director, File resources, TWTModule module) throws IOException {
        if (!director.isDirectory()) {
            throw new IOException("File " + director + " must be directory");
        }
        this.root = director;
        this.module = module;

        addURL(director.toURI().toURL());
        if (resources != null)
            addURL(resources.toURI().toURL());
    }

    @Override
    public TWTModule getModule() {
        return module;
    }

    @Override
    public void close() throws IOException {
        //
    }
    /*

    @Override
    public URL getResource(String name) {
        try {
            File res = new File(root, name);
            if (!res.isFile()) {
                return null;
            }
            return res.toURI().toURL();
        } catch (MalformedURLException ex) {
            return null;
        }
    }
*/

}
