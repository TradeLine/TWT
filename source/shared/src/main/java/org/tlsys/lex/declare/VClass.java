package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import org.tlsys.*;
import org.tlsys.lex.*;
import org.tlsys.sourcemap.SourcePoint;

import java.io.*;
import java.util.*;
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

    private static boolean isEqualArguments(List<VArgument> arg1, List<VArgument> arg2) {
        if (arg1.size() != arg2.size())
            return false;

        for (int i = 0; i < arg1.size(); i++) {
            if (arg1.get(i).getType() != arg2.get(i).getType())
                return false;
        }
        return true;
    }

    private static int calcCof(VMethod method, List<VClass> args) {
        int t = 0;
        for (int i = 0; i < args.size(); i++) {
            int r = TypeUtil.getCastLevelResult(args.get(i), method.getArguments().get(i).var ? method.getArguments().get(i).getType().getArrayClass() : method.getArguments().get(i).getType());
            if (r < 0)
                return -1;
            t += r;
        }
        return t;
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

    public void addChild(VClass clazz) {
        childs.add(clazz);
    }

    /*
    public VClass() {
        classSymbol = null;
        classLoader.classes.add(this);
    }
    */

    public String getSimpleRealName() {
        return realSimpleName;
    }

    public String getRealName() {
        if (parentContext instanceof VPackage) {
            VPackage p = (VPackage) parentContext;
            if (p.getSimpleName() == null)
                return getSimpleRealName();
            return p.getName() + "." + getSimpleRealName();
        }

        if (parentContext instanceof VClass) {
            VClass c = (VClass) parentContext;
            return c.getRealName() + "$" + getSimpleRealName();
        }

        return parentContext.toString() + "$" + getSimpleRealName();
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

    private boolean equalArgs(VExecute exe, List<VClass> args) {
        for (int i = 0; i < exe.getArguments().size(); i++) {
            VArgument a = exe.getArguments().get(i);
            if (a.var) {
                ArrayClass ac = (ArrayClass) a.getType();
                if (i >= args.size())
                    return true;
                if (args.size() == exe.getArguments().size() && args.get(i) instanceof ArrayClass && args.get(i) == ac)
                    return true;

                for (int j = i; j < args.size(); j++) {
                    if (!args.get(j).isParent(ac.getComponent()))
                        return false;
                }
                return true;
            }
            if (i >= args.size())
                return false;
            if (args.get(i) instanceof NullClass)
                continue;
            if (!args.get(i).isParent(a.getType()))
                return false;
        }
        return exe.getArguments().size() == args.size();
    }

    public VConstructor getConstructor(SourcePoint point, VClass... args) throws MethodNotFoundException {
        return getConstructor(Arrays.asList(args), point);
    }

    public VConstructor getConstructor(List<VClass> args, SourcePoint point) throws MethodNotFoundException {
        for (VConstructor v : constructors)
            if (equalArgs(v, args)) {
                return v;
            }

        throw new MethodNotFoundException(this, "<init>", args, point);
    }

    public VConstructor getConstructor(Symbol.MethodSymbol symbol, SourcePoint point) throws MethodNotFoundException {
        try {
            return getConstructor(getMethodArgs(symbol, point), point);
        } catch (VClassNotFoundException e) {
            throw new MethodNotFoundException(symbol, point);
        }
    }

    public VMethod getMethod(String name, SourcePoint point, VClass... args) throws MethodNotFoundException {
        return getMethod(name, Arrays.asList(args), point);
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

    /*
    Правила выбора метода
        http://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.12

        На первой фазе (§15.12.2.2) performs overload resolution без нужды боксинга или анбоксинга, или методов, используемых переменное число аргшументов. Если если требуемый метод не найден, то переходим ко второй фазе.
        На второй фазе (§15.12.2.3) performs overload resolution с допустимым боксингом или анбоксингом, но без исользования методов с переменным числом аргументов. Если если требуемый метод не найден, то переходим к третей фазе.
        На третей фазе (§15.12.2.4) допускаются использовать методы с переменным числом аргументов, боксингом и анбоксингом.
     */

    public List<VMethod> getMethodByName(String name) {
        ArrayList<VMethod> methods = new ArrayList<>();

        for (VMethod m : this.methods) {
            if (m.isThis(name))
                methods.add(m);
        }

        if (extendsClass != null) {
            extendsClass.getMethodByName(name).forEach(e -> {
                if (!concateMethodWithArgs(methods, e.arguments))
                    methods.add(e);
            });
        }

        for (VClass c : implementsList) {
            c.getMethodByName(name).forEach(e -> {
                if (!concateMethodWithArgs(methods, e.arguments))
                    methods.add(e);
            });
            ;
        }

        return methods;
    }

    private void getMethodForSearch(String name, Collection<VMethod> out, List<VClass> args) {

        /*
        System.out.println("==Seach in " + getRealName() + " for " + name + ". Method count=" + methods.size());
        for (VClass cl : args)
            System.out.println("=>" + cl);

        */
        METHODS:
        for (VMethod v : methods) {
            if (!name.equals(v.getRunTimeName()) && !name.equals(v.alias)) {
                //System.out.println("BAD NAME! need " + name + " but have " + v);
                continue;
            }

            if (!equalArgs(v, args)) {
                //System.out.println("BAD ARGUMENT " + v);
                continue;
            }

            if (out.contains(v)) {
                //System.out.println("FUNCTION ALLREADY ADDED");
                continue;
            }

            for (VMethod m : v.getReplaced()) {
                if (out.contains(out)) {
                    //System.out.println("Method replaced this " + v + " allready added " + m);
                    continue METHODS;
                }
            }


            if (out.contains(v.getReplace())) {
                //System.out.println("remove " + v.getReplace());
                out.remove(v.getReplace());
            }
            //System.out.println("add " + v);
            out.add(v);
        }

        if (extendsClass != null)
            extendsClass.getMethodForSearch(name, out, args);

        for (VClass v : implementsList)
            v.getMethodForSearch(name, out, args);
    }

    public VMethod getMethod(String name, List<VClass> args, SourcePoint point) throws MethodNotFoundException {
        final List<VMethod> methods = Collections.synchronizedList(new ArrayList<>());
/*
        System.out.println("Seach " + getRealName() + "." + name + " with args:\t\ton " + point + " method count=" + this.methods.size());
        for (VClass v : args) {
            System.out.println("=>" + v.getRealName());
        }

        System.out.println("-------------------");
*/
        this.methods.parallelStream().forEach(v -> {
            if (!name.equals(v.getRunTimeName()) && !name.equals(v.alias))
                return;

            if (!equalArgs(v, args)) {
                //System.out.println("BAD ARGUMENTS " + v);
                return;
            }
            //System.out.println("add " + v);
            methods.add(v);
        });

        implementsList.parallelStream().forEach(c -> {
            c.getMethodForSearch(name, methods, args);
        });

        if (extendsClass != null) {
            extendsClass.getMethodForSearch(name, methods, args);
        }

        //------------------АНАЛИЗ АРГУМЕНТОВ МЕТОДОВ------------------//


        //первая фаза: ищем методы с полным совпадением
        METHOD:
        for (VMethod m : methods) {
            if (m.getArguments().size() != args.size())
                continue;
            for (int i = 0; i < m.getArguments().size(); i++) {
                if (m.getArguments().get(i).getType() != args.get(i))
                    continue METHOD;
            }
            //System.out.println("FINDED " + m);
            return m;
        }

        //вторая фаза
        final ArrayList<VMethod> p2 = new ArrayList<>(methods);
        p2.removeIf(e -> e.getArguments().stream().filter(arg -> arg.var).count() > 0 && e.getArguments().size() != args.size());
        if (!p2.isEmpty()) {
            p2.sort((v1, v2) -> {
                return calcCof(v1, args) - calcCof(v2, args);
            });

            //System.out.println("FINDED " + p2.get(0));
            return p2.get(0);
        }

        //TODO: третяя фаза

        throw new MethodNotFoundException(this, name, args, point);

        /*
        //Правило #1
        if (methods.size() == 1)
            return methods.get(0);

        if (methods.parallelStream().filter(e -> e.getArguments().parallelStream().filter(arg -> arg.var).count() > 0).count() > 0) {//если среди найденых методов есть те, которые содержат переменное число аргументов
            if (methods.parallelStream().filter(e -> e.getArguments().parallelStream().filter(arg -> arg.var).count() == 0).count() > 0) {//если среди найденых есть методы, не содержащие переменное число аргументов
                methods.removeIf(e -> e.getArguments().parallelStream().filter(arg -> arg.var).count() > 0);//удаляем все методы, содержащие переменное число аргументов. Правило #2
            }
            //Ищем методы, у которых ПОЛНОСТЬЮ совпадают типы аргументов с теми, с которыми вызывают. Правило #3
            METHOD:
            for (VMethod m : methods) {
                if (m.getArguments().size() != args.size())
                    continue;
                for (int i = 0; i < m.getArguments().size(); i++) {
                    if (m.getArguments().get(i).getType() != args.get(i))
                        continue METHOD;
                }
                return m;
            }
        } else {

        }

        throw new MethodNotFoundException(this, name, args, point);
        */

        /*
        for (VMethod v : methods) {
            if (!name.equals(v.getRunTimeName()) && !name.equals(v.alias))
                continue;
            if (equalArgs(v, args))
                return v;
        }

        if (extendsClass != null) {
            try {
                return extendsClass.getMethod(name, args, point);
            } catch (MethodNotFoundException e) {
            }
        }

        for (VClass c : implementsList) {
            try {
                return c.getMethod(name, args, point);
            } catch (MethodNotFoundException e) {
            }
        }

        throw new MethodNotFoundException(this, name, args, point);
        */
    }


    public VMethod getMethod(Symbol.MethodSymbol symbol, SourcePoint point) throws MethodNotFoundException {
        Objects.requireNonNull(symbol, "Argument symbol is NULL");
        try {
            return getMethod(symbol.name.toString(), getMethodArgs(symbol, point), point);
        } catch (VClassNotFoundException e) {
            throw new MethodNotFoundException(symbol, point);
        }
    }


    private List<VClass> getMethodArgs(Symbol.MethodSymbol symbol, SourcePoint point) throws VClassNotFoundException {
        List<VClass> args = new ArrayList<>();

        if (symbol.name.toString().equals("<init>"))
            getDependencyParent().ifPresent(e -> args.add(e));

        if (symbol.params != null) {
            for (Symbol.VarSymbol e : symbol.params) {
                args.add(TypeUtil.loadClass(getClassLoader(), e.type, point));
            }
        } else if (symbol.erasure_field != null) {
            Type.MethodType mt = (Type.MethodType) symbol.erasure_field;
            for (Type t : mt.argtypes) {
                args.add(TypeUtil.loadClass(getClassLoader(), t, point));
            }
        } else if (symbol.type != null && symbol.type instanceof Type.ForAll) {
            Type.ForAll fa = (Type.ForAll) symbol.type;
            for (Type t : fa.qtype.getParameterTypes()) {
                args.add(TypeUtil.loadClass(getClassLoader(), t, point));
            }
        } else if (symbol.type != null && symbol.type instanceof Type.MethodType) {
            Type.MethodType fa = (Type.MethodType) symbol.type;
            for (Type t : fa.argtypes) {
                args.add(TypeUtil.loadClass(getClassLoader(), t, point));
            }
        }
        return args;
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
