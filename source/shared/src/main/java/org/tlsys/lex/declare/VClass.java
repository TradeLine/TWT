package org.tlsys.lex.declare;

import org.tlsys.ClassModificator;
import org.tlsys.HavinSourceStart;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.Collect;
import org.tlsys.lex.Context;
import org.tlsys.lex.Using;
import org.tlsys.lex.VLex;
import org.tlsys.sourcemap.SourcePoint;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class VClass extends VLex implements Member, Using, Context, Serializable, CodeDynLoad, HavinSourceStart {

    private static final long serialVersionUID = 4915815860381948883L;
    private static final ThreadLocal<VClassLoader> currentClassLoader = new ThreadLocal<>();
    private static final int VCLASS = 2;
    private static final int REF = 3;
    private final SourcePoint startPoint;
    public String fullName;
    public String name;
    public String alias;
    public String codeGenerator = null;
    public VClass extendsClass;
    public String castGenerator;
    public ArrayList<VClass> implementsList = new ArrayList<>();
    //public String realName;
    public String domNode;
    public boolean force;
    public ArrayList<VConstructor> constructors = new ArrayList<>();
    public ArrayList<VMethod> methods = new ArrayList<>();
    public ArrayList<StaticBlock> statics = new ArrayList<>();
    protected String realSimpleName;
    protected Context parentContext;
    protected ArrayList<VField> fields = new ArrayList<>();
    protected transient String cashRealname = null;
    private List<VClass> childs = new ArrayList<VClass>();
    private List<ClassModificator> mods = new ArrayList<>();
    private transient VClassLoader classLoader;
    private int modificators;
    private VClass parent;
    //private VField parentVar;
    private transient Class javaClass;

    protected VClass(String realSimpleName, SourcePoint startPoint) {
        this.realSimpleName = realSimpleName;
        this.startPoint = startPoint;
    }

    public VClass(String realSimpleName, Context parentContext, VClass parent, SourcePoint startPoint) {
        this.realSimpleName = realSimpleName;
        this.parentContext = Objects.requireNonNull(parentContext, "Parent content is NULL");
        this.parent = parent;
        this.startPoint = startPoint;
    }

    public static VClassLoader getCurrentClassLoader() {
        return currentClassLoader.get();
    }

    public static void setCurrentClassLoader(VClassLoader classLoader) {
        currentClassLoader.set(classLoader);
    }

    public List<ClassModificator> getMods() {
        return mods;
    }

    public VClass addMod(ClassModificator modificator) {
        if (mods.add(modificator))
            modificator.onAdd(this);
        return this;
    }

    public VClass removeMod(ClassModificator modificator) {
        if (mods.remove(modificator))
            modificator.onRemove(this);
        return this;
    }

    /*
    public VClass() {
        classSymbol = null;
        classLoader.classes.add(this);
    }
    */

    public void addChild(VClass clazz) {
        childs.add(clazz);
    }

    public String getSimpleRealName() {
        return realSimpleName;
    }

    public String getRealName() {
        if (cashRealname != null)
            return cashRealname;
        if (parentContext instanceof VPackage) {
            VPackage p = (VPackage) parentContext;
            if (p.getSimpleName() == null) {
                cashRealname = getSimpleRealName();
                return cashRealname;
            }
            cashRealname = p.getName() + "." + getSimpleRealName();
            return cashRealname;
        }

        if (parentContext instanceof VClass) {
            VClass c = (VClass) parentContext;
            cashRealname = c.getRealName() + "$" + getSimpleRealName();
            return cashRealname;
        }

        cashRealname = parentContext.toString() + "$" + getSimpleRealName();
        return cashRealname;
    }

    public void visit(ReplaceVisiter replaceControl) {
        for (VMethod m : methods)
            m.visit(replaceControl);
    }

    public Optional<ClassModificator> getModificator(Predicate<ClassModificator> test) {
        for (ClassModificator cm : mods) {
            if (test.test(cm))
                return Optional.of(cm);
        }
        return Optional.empty();
    }

    public List<VField> getLocalFields() {
        List<VField> f = new ArrayList<>(fields);
        for (ClassModificator cm : mods)
            f = cm.getFields(f);
        return f;
    }

    public Context getParentContext() {
        return parentContext;
    }

    public VClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(VClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public int getModificators() {
        return modificators;
    }

    public void setModificators(int modificators) {
        this.modificators = modificators;
    }

    @Override
    public boolean isThis(String name) {
        Objects.requireNonNull(name, "Name is NULL");
        boolean b = this.fullName.equals(name)
                || name.equals(this.alias)
                || name.equals(getRealName());
        return b;
    }

    public Optional<VClass> getDependencyParent() {
        try {
            return getDependencyParent(getClassLoader().loadClass(Enum.class.getName(), null));
        } catch (VClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<VClass> getDependencyParent(VClass enumClass) {
        if (getParentContext() instanceof VClass
                && !java.lang.reflect.Modifier.isInterface(getModificators())
                && !java.lang.reflect.Modifier.isStatic(getModificators())
                && !isParent(enumClass))
            return Optional.of((VClass) getParentContext());
        return Optional.empty();
    }

    @Override
    public VClass getParent() {
        return parent;
    }

    @Override
    public void getUsing(Collect c) {
        c.add(extendsClass);
        for (VClass v : implementsList)
            c.add(v);
        for (VField f : getLocalFields()) {
            c.add(f);
        }

        for (StaticBlock v : statics)
            c.add(v);
    }

    public Collect getAllUsing() {
        Collect c = Collect.create();
        getUsing(c);
        for (StaticBlock v : statics)
            c.add(v);
        for (VConstructor v : constructors)
            c.add(v);
        for (VMethod v : methods)
            c.add(v);
        return c;
    }

    @Override
    public String toString() {
        return getRealName();
    }

    public int getParentCount(VClass clazz, int level) {
        if (clazz == this)
            return level;
        if (extendsClass == clazz)
            return level + 1;
        else if (extendsClass != null) {
            int r = extendsClass.getParentCount(clazz, level);
            if (r >= 0)
                return r + 1;
        }
        for (VClass e : implementsList) {
            if (e == clazz)
                return level + 1;
            int r = e.getParentCount(clazz, level);
            if (r >= 0)
                return r + 1;
        }
        return -1;
    }

    public boolean isParent(VClass clazz) {
        return getParentCount(clazz, 0) >= 0;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        for (VClass p : childs) {
            if (name.equals(p.getSimpleRealName()) && searchIn.test(p))
                return Optional.of(p);
            if (p.isThis(name) && searchIn.test(p))
                return Optional.of(p);
        }
        for (VField f : getLocalFields()) {
            if (!searchIn.test(f))
                continue;
            if (name.equals(f.getRealName()) || name.equals(f.getAliasName()))
                return Optional.of(f);
        }
        if (extendsClass != null) {
            Optional<Context> v = extendsClass.find(name, searchIn);
            if (v.isPresent())
                return v;
        }
        for (VClass c : implementsList) {
            Optional<Context> v = c.find(name, searchIn);
            if (v.isPresent())
                return v;
        }
        if (getParentContext() != null)
            return getParentContext().find(name, searchIn);
        return Optional.empty();
    }

    public ArrayClass getArrayClass() {
        if (getClassLoader() == null)
            throw new NullPointerException("ClassLoader not set for class " + getRealName());
        return getClassLoader().getArrayClass(this);
    }

    @Override
    public void saveCode(ObjectOutputStream outputStream) throws IOException {
        for (VConstructor c : constructors)
            c.saveCode(outputStream);

        for (VMethod c : methods)
            c.saveCode(outputStream);

        for (VField c : getLocalFields())
            c.saveCode(outputStream);
    }

    @Override
    public void loadCode(ObjectInputStream outputStream) throws IOException, ClassNotFoundException {
        for (VConstructor c : constructors)
            c.loadCode(outputStream);

        for (VMethod c : methods)
            c.loadCode(outputStream);

        for (VField c : getLocalFields())
            c.loadCode(outputStream);
    }

    Object writeReplace() throws ObjectStreamException {
        if (this instanceof ArrayClass)
            return new ArrayRef(((ArrayClass) this).getComponent());
        if (getClassLoader() != getCurrentClassLoader())
            return new ClassRef(fullName);
        return this;
    }

    /**
     * Возвращает реальный Java класс из которого был сгенерирован этот слепок
     *
     * @return реальный Java класс
     * @throws ClassNotFoundException возникает в случае если реальный Java класс не найден
     */
    public Class getJavaClass() {
        try {
            if (javaClass == null)
                javaClass = getClassLoader().getJavaClassLoader().loadClass(getRealName());
            return javaClass;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeObject(ObjectOutputStream out) throws Exception {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws Exception {
        if (this instanceof ArrayClass)
            throw new RuntimeException("Not supported");
        setClassLoader(getCurrentClassLoader());
        in.defaultReadObject();
    }

    public VField getField(String name, SourcePoint point) throws VFieldNotFoundException {
        for (VField f : getLocalFields()) {
            if (name.equals(f.getRealName()) || name.equals(f.getAliasName()))
                return f;
        }
        throw new VFieldNotFoundException(this, name, point);
    }

    public void addLocalField(VField v) {
        fields.add(v);
    }

    @Override
    public SourcePoint getStartPoint() {
        return startPoint;
    }

    private static class ClassRef implements Serializable {
        private static final long serialVersionUID = 7210195275588742049L;

        private String name;

        public ClassRef(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        Object readResolve() throws Exception {
            for (VClassLoader cl : getCurrentClassLoader().parents) {
                try {
                    return cl.loadClass(getName(), null);
                } catch (VClassNotFoundException e) {

                }
            }
            VClassLoader ll = getCurrentClassLoader();
            throw new VClassNotFoundException(getName(), null);
        }
    }

    private static class ArrayRef implements Serializable {
        private static final long serialVersionUID = 5499514183335556544L;
        private VClass component;

        public ArrayRef(VClass component) {
            this.component = component;
        }

        public VClass getComponent() {
            return component;
        }

        Object readResolve() throws Exception {
            return getComponent().getArrayClass();
        }
    }


}
