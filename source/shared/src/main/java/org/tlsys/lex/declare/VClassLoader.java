package org.tlsys.lex.declare;

import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.CompileException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class VClassLoader implements Serializable {
    private static final long serialVersionUID = -8477730129932068195L;
    private static ThreadLocal<List<VClassLoader>> parentList = new ThreadLocal<>();
    private final VPackage rootPackage = new VPackage(null, null);
    public ArrayList<VClass> classes = new ArrayList<>();
    public transient ArrayList<VClassLoader> parents = new ArrayList<>();
    private String name;
    private transient HashMap<VClass, ArrayClass> arrays = new HashMap<>();
    private transient ClassLoader javaClassLoader;
    private transient boolean loading = false;
    private transient LinkedList<ArrayLazyInit> lazy;

    public VClassLoader(String name) {
        this.name = name;
    }

    public VClassLoader() {
    }

    public static List<VClassLoader> getParentList() {
        return parentList.get();
    }

    public static void setParentList(List<VClassLoader> parentList) {
        VClassLoader.parentList.set(parentList);
    }

    public VPackage getRootPackage() {
        return rootPackage;
    }

    public ClassLoader getJavaClassLoader() {
        return javaClassLoader;
    }

    public void setJavaClassLoader(ClassLoader javaClassLoader) {
        this.javaClassLoader = javaClassLoader;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addClass(VClass cl) {
        classes.add(cl);
        cl.setClassLoader(this);
    }

    public ArrayClass getArrayClass(VClass clazz) {
        if (clazz.getClassLoader() != this)
            throw new IllegalStateException("Bad ArrayClasses ClassLoader");

        ArrayClass ar = arrays.get(clazz);
        if (ar != null)
            return ar;

        if (loading) {
            ar = new ArrayClass(clazz);
            lazy.add(new ArrayLazyInit(ar));
        } else {
            try {
                ar = new ArrayClass(clazz, loadClass(int.class.getName(), null));
            } catch (VClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        ar.setClassLoader(this);
        arrays.put(clazz, ar);
        return ar;
    }

    public VClass loadClass(String name, SourcePoint point) throws VClassNotFoundException {
        //name = name.replace('$','.');
        if (classes == null)
            classes = new ArrayList<>();
        for (VClass v : classes) {
            if (v.isThis(name)) {
                return v;
            }
        }

        for (VClassLoader v : parents) {
            try {
                return v.loadClass(name, point);
            } catch (VClassNotFoundException e) {

            }
        }

        throw new VClassNotFoundException(name, point);
    }

    private void writeObject(ObjectOutputStream out) throws java.io.IOException {
        VClass.setCurrentClassLoader(this);
        out.defaultWriteObject();
        for (VClass cl : classes)
            cl.saveCode(out);
        out.flush();
    }

    private void readObject(ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        lazy = new LinkedList<>();
        this.parents = new ArrayList<>();
        this.arrays = new HashMap<>();

        if (parentList == null)
            throw new IllegalStateException("parents not set");
        VClass.setCurrentClassLoader(this);
        this.parents.addAll(parentList.get());
        loading = true;
        in.defaultReadObject();



        for (VClass cl : classes)
            cl.loadCode(in);
        loading = false;

        for (ArrayLazyInit a : lazy)
            a.init();
        lazy = null;
    }

    private class ArrayLazyInit {
        private ArrayClass clazz;

        public ArrayLazyInit(ArrayClass clazz) {
            this.clazz = clazz;
        }

        public void init() {
            try {
                clazz.init(loadClass(int.class.getName(), null));
            } catch (CompileException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*
    private Object readResolve() {

    }
    */
}
