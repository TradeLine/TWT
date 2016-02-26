package org.tlsys.twt;

import org.tlsys.lex.declare.VClassLoader;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Logger;

public abstract class DClassLoader extends URLClassLoader {

    private static final Logger LOG = Logger.getLogger(DClassLoader.class.getName());

    public static final String JSLIB = "twt.data";

    public DClassLoader(DLoader loader) {
        super(new URL[]{});
        this.loader = loader;
    }

    public DLoader getLoader() {
        return loader;
    }

    public abstract Set<DClassLoader> getParents();
    private boolean haveJSLib = true;
    private VClassLoader jsClassLoader;
    private final DLoader loader;

    public abstract String getName();

    public VClassLoader getJsClassLoader() {
        if (jsClassLoader != null)
            return jsClassLoader;
        if (!haveJSLib)
            return null;
        try (InputStream is = getResourceAsStream(JSLIB)) {
            if (is == null) {
                haveJSLib = false;
                return null;
            }

            ArrayList<VClassLoader> cl = new ArrayList<>(getParents().size());
            for (DClassLoader p : getParents()) {
                VClassLoader vv = p.getJsClassLoader();
                if (vv != null)
                    cl.add(vv);
            }
            VClassLoader.setParentList(cl);
            ObjectInputStream ois = new ObjectInputStream(is);
            jsClassLoader = (VClassLoader) ois.readObject();
            jsClassLoader.setJavaClassLoader(this);

            return jsClassLoader;
        } catch (IOException e) {
            throw new RuntimeException(getName(), e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(getName(), e);
        }
    }

    public void setJsClassLoader(VClassLoader jsClassLoader) {
        this.jsClassLoader = Objects.requireNonNull(jsClassLoader);
        haveJSLib = true;
    }

    public void saveJSClassLoader(OutputStream outputStream) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(jsClassLoader);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Objects.requireNonNull(name, "Argument \"name\" is NULL");
        name = name.trim();
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {

        }
        try {
            System.out.print(closed + ">>");
            if (this instanceof JarDClassLoader) {
                System.out.print("JAR " + ((JarDClassLoader)this).getFile());
            }else
                System.out.print(getName());
            System.out.print(" class \"" + name + "\"...");
            Class cl = super.loadClass(name);
            System.out.println("FOUNDED");
            return cl;
        } catch (ClassNotFoundException e) {
            System.out.println("NOT FOUNDED");
        }
        for (ClassLoader cl : getParents()) {
            try {
                return cl.loadClass(name);
            } catch (ClassNotFoundException e) {
                //
            }
        }
        return getSystemClassLoader().loadClass(name);
    }

    private boolean closed = false;

    @Override
    public void close() throws IOException {
        super.close();
        closed = true;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        final Set<URL> urls = new HashSet<>();
        for (ClassLoader cl : getParents()) {
            urls.addAll(Collections.list(cl.getResources(name)));
        }
        urls.addAll(Collections.list(super.getResources(name)));
        urls.addAll(Collections.list(getSystemClassLoader().getResources(name)));
        return Collections.enumeration(urls);
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        final HashSet<URL> urls = new HashSet<>();
        for (DClassLoader cl : getParents()) {
            urls.addAll(Collections.list(cl.findResources(name)));
        }
        urls.addAll(Collections.list(super.findResources(name)));
        return Collections.enumeration(urls);
    }
}
