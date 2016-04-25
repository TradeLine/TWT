package org.tlsys.RT;

import org.tlsys.JavaSourceSet;
import org.tlsys.twt.expressions.AnntationItem;
import org.tlsys.twt.members.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class TestClass implements VClass {

    private static final long serialVersionUID = 8022740401644885507L;
    private final TClassLoader classLoader;
    private VMember parent;
    private int modifiers;

    private boolean primitive;
    private String simpleName;
    private String realtimeName;
    private VClass superClass;

    public TestClass(TClassLoader classLoader, String simpleName, String realtimeName, VClass superClass) {
        this.parent = parent;
        this.classLoader = classLoader;
        this.simpleName = simpleName;
        this.realtimeName = realtimeName;
        this.superClass = superClass;
    }

    private static final VPackage getPackage(String name, JavaSourceSet javaSourceSet) {
        if (name == null || name.isEmpty())
            return javaSourceSet.getRootPackage();
        String[] list = name.toString().split("\\.");
        VPackage p = javaSourceSet.getRootPackage();
        for (String s : list) {
            Optional<VPackage> op = p.getChild(e -> {
                if (e instanceof VPackage) {
                    VPackage pp = (VPackage) e;
                    if (pp.getName().equals(s)) {
                        return true;
                    }
                }
                return false;
            });
            if (op.isPresent())
                p = op.get();
            else {
                VPackage ppp = new VPackage(s, p);
                p.add(ppp);
                p = ppp;
            }
        }
        return p;
    }

    public static final <T extends TestClass> T inject(String packageName, T clazz, JavaSourceSet javaSourceSet) {
        javaSourceSet.getClassLoader().addClass(clazz);
        VPackage p = getPackage(packageName, javaSourceSet);
        p.add(clazz);
        clazz.setParent(p);
        return clazz;
    }

    @Override
    public String getSimpleName() {
        return null;
    }

    @Override
    public String getSimpleRealTimeName() {
        int p = getRealTimeName().lastIndexOf(".");
        int p1 = getRealTimeName().lastIndexOf("$");

        if (p != -1 && (p > p1 || p1 == -1))
            return getRealTimeName().substring(p + 1);

        if (p1 != -1 && (p1 > p || p == -1))
            return getRealTimeName().substring(p1 + 1);

        return getRealTimeName();
    }

    @Override
    public String getRealTimeName() {
        return realtimeName;
    }

    @Override
    public VClass getSuperClass() {
        return superClass;
    }

    @Override
    public Optional<VMethod> findMethod(String name, MehtodSearchRequest request) {
        return null;
    }

    @Override
    public TClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public Optional<TField> getField(String name) {
        return null;
    }

    @Override
    public boolean isPrimitive() {
        return primitive;
    }

    public TestClass setPrimitive(boolean primitive) {
        this.primitive = primitive;
        return this;
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    public TestClass setModifiers(int modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    @Override
    public boolean add(VMember member) {
        return false;
    }

    @Override
    public boolean remove(VMember member) {
        return false;
    }

    @Override
    public <T extends VMember> Optional<T> getChild(Predicate<VMember> predicate) {
        return null;
    }

    @Override
    public VMember getParent() {
        return parent;
    }

    public void setParent(VMember parent) {
        this.parent = parent;
    }

    @Override
    public List<AnntationItem> getList() {
        return null;
    }
}
