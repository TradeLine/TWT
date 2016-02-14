package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import org.tlsys.twt.CompileException;

import javax.lang.model.type.NullType;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.UnaryOperator;

public class VClassLoader implements Serializable {
    private static final long serialVersionUID = -8477730129932068195L;

    private String name;

    public ArrayList<VClass> classes = new ArrayList<>();
    public transient ArrayList<VClassLoader> parents = new ArrayList<>();
    private transient HashMap<VClass, ArrayClass> arrays = new HashMap<>();
    private transient ClassLoader javaClassLoader;

    public ClassLoader getJavaClassLoader() {
        return javaClassLoader;
    }

    public void setJavaClassLoader(ClassLoader javaClassLoader) {
        this.javaClassLoader = javaClassLoader;
    }

    public VClassLoader(String name) {
        this.name = name;
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

    private transient boolean loading = false;

    public VClassLoader() {
    }

    private class ArrayLazyInit {
        private ArrayClass clazz;

        public ArrayLazyInit(ArrayClass clazz) {
            this.clazz = clazz;
        }

        public void init() {
            try {
                clazz.init(loadClass(int.class.getName()));
            } catch (CompileException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private transient LinkedList<ArrayLazyInit> lazy;

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
                ar = new ArrayClass(clazz, loadClass(int.class.getName()));
            } catch (VClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        ar.setClassLoader(this);
        arrays.put(clazz, ar);
        return ar;
    }

    public VClass loadClass(String name) throws VClassNotFoundException{
        name = name.replace('$','.');
        if (classes == null)
            classes = new ArrayList<>();
        for (VClass v : classes) {
            if (v.isThis(name)) {
                return v;
            }
        }

        for (VClassLoader v : parents) {
            try {
                return v.loadClass(name);
            } catch (VClassNotFoundException e) {

            }
        }

        throw new VClassNotFoundException(name);
    }

    private void writeObject(ObjectOutputStream out) throws java.io.IOException {
        VClass.setCurrentClassLoader(this);
        out.defaultWriteObject();
        for (VClass cl : classes)
            cl.saveCode(out);
        out.flush();
    }

    private static ThreadLocal<List<VClassLoader>> parentList = new ThreadLocal<>();

    public static List<VClassLoader> getParentList() {
        return parentList.get();
    }

    public static void setParentList(List<VClassLoader> parentList) {
        VClassLoader.parentList.set(parentList);
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

    /*
    private Object readResolve() {

    }
    */
}
