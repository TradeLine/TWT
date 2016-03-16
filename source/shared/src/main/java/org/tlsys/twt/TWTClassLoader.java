/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tlsys.twt;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 *
 * @author subochev
 */
public abstract class TWTClassLoader extends URLClassLoader {

    public TWTClassLoader() {
        super(new URL[]{});
    }

    public abstract TWTModule getModule();
    final List<TWTClassLoader> parents = new ArrayList<>();

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        final Set<URL> urls = new HashSet<>();
        for (ClassLoader cl : parents) {
            urls.addAll(Collections.list(cl.getResources(name)));
        }
        /*
        URL ur = getResource(name);
        if (ur != null)
            urls.add(ur);
         */
        urls.addAll(Collections.list(super.getResources(name)));
        urls.addAll(Collections.list(getSystemClassLoader().getResources(name)));
        return Collections.enumeration(urls);
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        final HashSet<URL> urls = new HashSet<>();
        for (TWTClassLoader cl : parents) {
            urls.addAll(Collections.list(cl.findResources(name)));
        }
        urls.addAll(Collections.list(super.findResources(name)));
        return Collections.enumeration(urls);
    }

    @Override
    public String toString() {
        return "TWTClassLoader{" + getModule().getName() + '}';
    }

    @Override
    public URL findResource(String name) {
        return super.findResource(name); //To change body of generated methods, choose Tools | Templates.
    }

    /*1
    protected Class<?> loadClass1(String name) throws ClassNotFoundException {
        Class o = classes.get(name);
        if (o != null) {
            return o;
        }
        try (InputStream in = getResourceAsStream(name.replace('.', '/'))) {
            if (in == null) {
                throw new ClassNotFoundException(name);
            }
            try (ByteArrayOutputStream data = new ByteArrayOutputStream()) {
                int len = 0;
                byte[] buffer = new byte[512];
                while ((len = in.read(buffer)) != -1) {
                    data.write(buffer, 0, len);
                }
                buffer = data.toByteArray();
                o = defineClass(name, buffer, 0, buffer.length);
                classes.put(name, o);
                return o;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
     */
    
    
    
    //private final HashMap<String, Class> classes = new HashMap<>();
    
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
        return Thread.currentThread().getContextClassLoader().loadClass(name);
        } catch(ClassNotFoundException e){}
        //if (classes.containsKey(name))
            //return classes.get(name);
        try {
            Class cl = super.loadClass(name);
            //classes.put(name, cl);
            return cl;
        } catch (ClassNotFoundException e) {
        }

        for (TWTClassLoader cl : parents) {
            try {
                Class c = cl.loadClass(name);
                //classes.put(name, c);
                return c;
            } catch (ClassNotFoundException e) {
            }
        }

        Class cl = getSystemClassLoader().loadClass(name);
        //classes.put(name, cl);
        return cl;
    }

    /*
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Objects.requireNonNull(name, "Argument \"name\" is NULL");
        name = name.trim();
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {

        }
        try {
            Class cl = super.loadClass(name);
            return cl;
        } catch (ClassNotFoundException e) {
        }
        for (ClassLoader cl : parents) {
            try {
                return cl.loadClass(name);
            } catch (ClassNotFoundException e) {
                //
            }
        }
        return getSystemClassLoader().loadClass(name);
    }
*/

    //public abstract void close() throws IOException;
}
