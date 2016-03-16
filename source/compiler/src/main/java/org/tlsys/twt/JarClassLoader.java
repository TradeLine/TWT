/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tlsys.twt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author subochev
 */
public class JarClassLoader extends TWTClassLoader {

    private final TWTModule module;

    public JarClassLoader(File jarFile, TWTModule module) throws IOException {
        this.module = module;
        
        addURL(jarFile.toURI().toURL());
    }

    @Override
    public TWTModule getModule() {
        return module;
    }
    
    

    /*
    private class JarURLHendler extends URLStreamHandler {

        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            return new RecourceConenction(u);
        }
    }

    private class RecourceConenction extends JarURLConnection {

        public RecourceConenction(URL url) throws MalformedURLException {
            super(url);
        }

        @Override
        public void connect() throws IOException {
            throw new UnsupportedOperationException("Not supported yet." + getURL()); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public InputStream getInputStream() throws IOException {
            try {
                JarEntry en = files.get(getURL().getPath().substring(1));
                if (en.isDirectory()) {
                    return null;
                }
                InputStream is = jar.getInputStream(en);
                return new InputStreamProxy(is);
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        @Override
        public JarEntry getJarEntry() throws IOException {
            return super.getJarEntry(); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public URL getJarFileURL() {
            return super.getJarFileURL(); //To change body of generated methods, choose Tools | Templates.
        }
        
        

        @Override
        public JarFile getJarFile() throws IOException {
            return jar;
        }

    }

    private final JarFile jar;
    
    private final String pathURLPath;
    private final HashMap<String, JarEntry> files = new HashMap<>();
    private final JarURLHendler hendler = new JarURLHendler();

    public JarClassLoader(File jarFile, TWTModule module) throws IOException {
        this.module = module;
        jar = new JarFile(jarFile);
        pathURLPath = jarFile.toURI().toURL().toString();

        Enumeration<JarEntry> list = jar.entries();
        while (list.hasMoreElements()) {
            JarEntry e = list.nextElement();
            String name = e.getName();
            if (e.isDirectory()) {
                name = name.substring(0, name.length() - 1);
            }
            files.put(name, e);
        }

        jar.entries();
    }

    public void close() throws IOException {
        jar.close();
    }

    private class InputStreamProxy extends InputStream {

        private final InputStream stream;

        public InputStreamProxy(InputStream stream) {
            this.stream = stream;
        }

        @Override
        public int read() throws IOException {
            return stream.read();
        }

        @Override
        public void close() throws IOException {
            stream.close(); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean markSupported() {
            return stream.markSupported(); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public synchronized void reset() throws IOException {
            stream.reset(); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public synchronized void mark(int readlimit) {
            stream.mark(readlimit); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int available() throws IOException {
            return stream.available(); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public long skip(long n) throws IOException {
            return stream.skip(n); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return stream.read(b, off, len); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int read(byte[] b) throws IOException {
            return stream.read(b); //To change body of generated methods, choose Tools | Templates.
        }

    }



    @Override
    public InputStream getResourceAsStream(String name) {
        try {
            JarEntry je = files.get(name);
            if (je == null) {
                return null;
            }
            if (je.isDirectory()) {
                return null;
            }
            return jar.getInputStream(je);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public URL getResource(String name) {
        if (!files.containsKey(name)) {
            return null;
        }
        try {
            return new URL(null, "jar:" + pathURLPath + "!/" + name, hendler);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
     */
}
