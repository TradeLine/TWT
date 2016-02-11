package org.tlsys.twt;

import org.tlsys.lex.declare.VClassLoader;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public abstract class DClassLoader extends URLClassLoader {

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
}
