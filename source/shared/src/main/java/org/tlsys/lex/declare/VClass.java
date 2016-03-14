package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import org.tlsys.TypeUtil;
import org.tlsys.lex.*;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;

public class VClass extends VLex implements Member, Using, Context, Serializable, CodeDynLoad {

    private static final long serialVersionUID = 4915815860381948883L;
    private static final ThreadLocal<VClassLoader> currentClassLoader = new ThreadLocal<>();
    private static final int VCLASS = 2;
    private static final int REF = 3;
    private transient final Symbol.ClassSymbol classSymbol;
    public String fullName;
    public String name;
    public String alias;
    public String codeGenerator = null;
    public VClass extendsClass;
    private List<VClass> childs = new ArrayList<VClass>();
    public ArrayList<VClass> implementsList = new ArrayList<>();
    private transient VClassLoader classLoader;
    private int modificators;
    private VClass parent;
    //private VField parentVar;
    private transient Class javaClass;
    public String realName;
    public String domNode;
    public boolean force;

    public ArrayList<VField> fields = new ArrayList<>();
    public ArrayList<VConstructor> constructors = new ArrayList<>();
    public ArrayList<VMethod> methods = new ArrayList<>();
    public ArrayList<StaticBlock> statics = new ArrayList<>();

    public VClass() {
        classSymbol = null;
        classLoader.classes.add(this);
    }

    public VClass(VClass parent, Symbol.ClassSymbol classSymbol) {
        this.parent = parent;
        if (parent != null)
            parent.childs.add(this);
        this.classSymbol = classSymbol;
    }

    public static VClassLoader getCurrentClassLoader() {
        return currentClassLoader.get();
    }

    public static void setCurrentClassLoader(VClassLoader classLoader) {
        currentClassLoader.set(classLoader);
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
        if (fullName == null)
            System.out.println("123");
        return this.fullName.equals(name) || name.equals(this.alias);
    }

    public Optional<VClass> getDependencyParent() {
        try {
            return getDependencyParent(getClassLoader().loadClass(Enum.class.getName()));
        } catch (VClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<VClass> getDependencyParent(VClass enumClass) {
            if (getParent() != null
                    && !java.lang.reflect.Modifier.isInterface(getModificators())
                    && !java.lang.reflect.Modifier.isStatic(getModificators())
                    && !isParent(enumClass))
                return Optional.of(getParent());
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
        for (VField f : fields) {
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
        return "VClass{" +
                "fullName='" + fullName + '\'' +
                ", alias='" + alias + '\'' +
                '}';
    }

    public boolean isParent(VClass clazz) {
        if (clazz == this)
            return true;
        if (extendsClass == clazz)
            return true;
        else if (extendsClass != null && extendsClass.isParent(clazz))
            return true;
        for (VClass e : implementsList) {
            if (e == clazz)
                return true;
            if (e.isParent(clazz))
                return true;
        }
        return false;
    }

    private boolean equalArgs(VExecute exe, List<VClass> args) throws MethodNotFoundException {
        for (int i = 0; i < exe.arguments.size(); i++) {
            VArgument a = exe.arguments.get(i);
            if (a.var) {
                ArrayClass ac = (ArrayClass) a.getType();
                if (i >= args.size())
                    return true;
                if (args.size() == exe.arguments.size() && args.get(i) instanceof ArrayClass && args.get(i) == ac)
                    return true;

                for (int j = i; j < args.size(); j++) {
                    if (!args.get(j).isParent(ac.getComponent()))
                        return false;
                }
                return true;
            }
            if (i >= args.size())
                return false;
            if (!args.get(i).isParent(a.getType()))
                return false;
        }
        return exe.arguments.size() == args.size();
    }

    public VConstructor getConstructor(VClass... args) throws MethodNotFoundException {
        return getConstructor(Arrays.asList(args));
    }

    public VConstructor getConstructor(List<VClass> args) throws MethodNotFoundException {
        for (VConstructor v : constructors)
            if (equalArgs(v, args))
                return v;
        throw new MethodNotFoundException(this, "<init>", args);
    }

    public VConstructor getConstructor(Symbol.MethodSymbol symbol) throws MethodNotFoundException {
        try {
            return getConstructor(getMethodArgs(symbol));
        } catch (VClassNotFoundException e) {
            throw new MethodNotFoundException(symbol);
        }
    }

    public VMethod getMethod(String name, VClass... args) throws MethodNotFoundException {
        return getMethod(name, Arrays.asList(args));
    }

    private static boolean isEqualArguments(List<VArgument> arg1, List<VArgument> arg2) {
        if (arg1.size() != arg2.size())
            return false;

        for (int i = 0; i < arg1.size(); i++) {
            if (arg1.get(i).getType() != arg2.get(i).getType())
                return false;
        }
        return true;
    }

    private boolean concateMethodWithArgs(Collection<VMethod> methods, List<VArgument> arguments) {
        Iterator<VMethod> it = methods.iterator();
        while (it.hasNext()) {
            VMethod m = it.next();
            if (isEqualArguments(m.arguments, arguments))
                return true;
        }
        return false;
    }

    public List<VMethod> getMethodByName(String name) {
        ArrayList<VMethod> methods = new ArrayList<>();

        for (VMethod m : this.methods) {
            if (m.isThis(name))
                methods.add(m);
        }

        if (extendsClass != null) {
            extendsClass.getMethodByName(name).forEach(e->{
                if (!concateMethodWithArgs(methods, e.arguments))
                    methods.add(e);
            });
        }

        for (VClass c : implementsList) {
            c.getMethodByName(name).forEach(e->{
                if (!concateMethodWithArgs(methods, e.arguments))
                    methods.add(e);
            });;
        }

        return methods;
    }

    public VMethod getMethod(String name, List<VClass> args) throws MethodNotFoundException {
        for (VMethod v : methods) {
            if (!name.equals(v.getRunTimeName()) && !name.equals(v.alias))
                continue;
            if (equalArgs(v, args))
                return v;
        }

        if (extendsClass != null) {
            try {
                return extendsClass.getMethod(name, args);
            } catch (MethodNotFoundException e) {
            }
        }

        for (VClass c : implementsList) {
            try {
                return c.getMethod(name, args);
            } catch (MethodNotFoundException e) {
            }
        }

        throw new MethodNotFoundException(this, alias, args);
    }

    public VMethod getMethod(Symbol.MethodSymbol symbol) throws MethodNotFoundException {
        Objects.requireNonNull(symbol, "Argument symbol is NULL");
        try {
            return getMethod(symbol.name.toString(), getMethodArgs(symbol));
        } catch (VClassNotFoundException e) {
            throw new MethodNotFoundException(symbol);
        }
    }

    private List<VClass> getMethodArgs(Symbol.MethodSymbol symbol) throws VClassNotFoundException {
        List<VClass> args = new ArrayList<>();
        
        //VClass enumClass = getClassLoader().loadClass(Enum.class.getName());
        if (symbol.name.toString().equals("<init>"))
            getDependencyParent().ifPresent(e->args.add(e));
        /*
        if (getParent() != null
                    && !java.lang.reflect.Modifier.isInterface(getModificators())
                    && !java.lang.reflect.Modifier.isStatic(getModificators())
                    && !isParent(enumClass))
                    */

        
        if (symbol.params != null) {
            for (Symbol.VarSymbol e : symbol.params) {
                args.add(TypeUtil.loadClass(getClassLoader(), e.type));
            }
        } else if (symbol.erasure_field != null) {
            Type.MethodType mt = (Type.MethodType) symbol.erasure_field;
            for (Type t : mt.argtypes) {
                args.add(TypeUtil.loadClass(getClassLoader(), t));
            }
        } else if (symbol.type != null && symbol.type instanceof Type.ForAll) {
            Type.ForAll fa = (Type.ForAll) symbol.type;
            for (Type t : fa.qtype.getParameterTypes()) {
                args.add(TypeUtil.loadClass(getClassLoader(), t));
            }
        } else if (symbol.type != null && symbol.type instanceof Type.MethodType) {
            Type.MethodType fa = (Type.MethodType) symbol.type;
            for (Type t : fa.argtypes) {
                args.add(TypeUtil.loadClass(getClassLoader(), t));
            }
        }
        return args;
    }

    @Override
    public Optional<SVar> find(String name, Predicate<Context> searchIn) {
        for (VField f : fields) {
            if (name.equals(f.getRealName()) || name.equals(f.getAliasName()))
                return Optional.of(f);
        }
        if (extendsClass != null && searchIn.test(extendsClass)) {
            Optional<SVar> v = extendsClass.find(name, searchIn);
            if (v.isPresent())
                return v;
        }
        for (VClass c : implementsList) {
            if (!searchIn.test(c))
                continue;
            Optional<SVar> v = c.find(name, searchIn);
            if (v.isPresent())
                return v;
        }
        if (getParent() != null && searchIn.test(getParent()))
            return getParent().find(name, searchIn);
        return Optional.empty();
    }

    public ArrayClass getArrayClass() {
        return Objects.requireNonNull(getClassLoader(), "ClassLoader not set for class " + fullName).getArrayClass(this);
    }
/*
    public VField getParentVar() {
        if (parentVar == null) {
            if (isInterface() || isStatic() || getParent() == null)
                throw new IllegalStateException("Class " + fullName + " can't have parent var");
            parentVar = new VField(getParent(), Modifier.PROTECTED | Modifier.FINAL, null, this);
            fields.add(parentVar);
        }
        return parentVar;
    }
    */

    @Override
    public void saveCode(ObjectOutputStream outputStream) throws IOException {
        for (VConstructor c : constructors)
            c.saveCode(outputStream);

        for (VMethod c : methods)
            c.saveCode(outputStream);

        for (VField c : fields)
            c.saveCode(outputStream);
    }

    @Override
    public void loadCode(ObjectInputStream outputStream) throws IOException, ClassNotFoundException {
        for (VConstructor c : constructors)
            c.loadCode(outputStream);

        for (VMethod c : methods)
            c.loadCode(outputStream);

        for (VField c : fields)
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
                javaClass = getClassLoader().getJavaClassLoader().loadClass(realName);
            return javaClass;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeObject(ObjectOutputStream out) throws Exception {
        out.defaultWriteObject();
        /*
        out.writeObject(constructors);
        out.writeObject(fields);
        out.writeObject(methods);
        out.writeObject(statics);
        */
    }

    private void readObject(ObjectInputStream in) throws Exception {
        if (this instanceof ArrayClass)
            throw new RuntimeException("Not supported");
        setClassLoader(getCurrentClassLoader());
        in.defaultReadObject();
        /*
        constructors = (ArrayList<VConstructor>) in.readObject();
        fields = (ArrayList<VField>) in.readObject();
        methods = (ArrayList<VMethod>) in.readObject();
        statics = (ArrayList<StaticBlock>) in.readObject();
        */
    }

    public VField getField(String name) throws VFieldNotFoundException {
        for (VField f : fields) {
            if (name.equals(f.getRealName()) || name.equals(f.getAliasName()))
                return f;
        }
        throw new VFieldNotFoundException(this, name);
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
                    return cl.loadClass(getName());
                } catch (VClassNotFoundException e) {

                }
            }
            VClassLoader ll = getCurrentClassLoader();
            throw new VClassNotFoundException(getName());
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
