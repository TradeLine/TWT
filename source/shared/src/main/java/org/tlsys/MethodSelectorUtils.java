package org.tlsys;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import org.tlsys.lex.MethodNotFoundException;
import org.tlsys.lex.declare.*;
import org.tlsys.sourcemap.SourcePoint;

import java.util.*;

public final class MethodSelectorUtils {
    private MethodSelectorUtils() {
    }

    /*
    Правила выбора метода
        http://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.12

        На первой фазе (§15.12.2.2) performs overload resolution без нужды боксинга или анбоксинга, или методов, используемых переменное число аргшументов. Если если требуемый метод не найден, то переходим ко второй фазе.
        На второй фазе (§15.12.2.3) performs overload resolution с допустимым боксингом или анбоксингом, но без исользования методов с переменным числом аргументов. Если если требуемый метод не найден, то переходим к третей фазе.
        На третей фазе (§15.12.2.4) допускаются использовать методы с переменным числом аргументов, боксингом и анбоксингом.
     */

    private static boolean isEqualArguments(List<VArgument> arg1, List<VArgument> arg2) {
        if (arg1.size() != arg2.size())
            return false;

        for (int i = 0; i < arg1.size(); i++) {
            if (arg1.get(i).getType() != arg2.get(i).getType())
                return false;
        }
        return true;
    }

    private static boolean concateMethodWithArgs(Collection<VMethod> methods, List<VArgument> arguments) {
        Iterator<VMethod> it = methods.iterator();
        while (it.hasNext()) {
            VMethod m = it.next();
            if (isEqualArguments(m.getSimpleArguments(), arguments))
                return true;
        }
        return false;
    }

    public static List<VMethod> getMethodByName(VClass self, String name) {
        ArrayList<VMethod> methods = new ArrayList<>();

        for (VMethod m : self.methods) {
            if (m.isThis(name))
                methods.add(m);
        }

        if (self.extendsClass != null) {
            getMethodByName(self.extendsClass, name).forEach(e -> {
                if (!concateMethodWithArgs(methods, e.getSimpleArguments()))
                    methods.add(e);
            });
        }

        for (VClass c : self.implementsList) {
            getMethodByName(c, name).forEach(e -> {
                if (!concateMethodWithArgs(methods, e.getSimpleArguments()))
                    methods.add(e);
            });
        }

        return methods;
    }


    public static VMethod getMethod(VClass self, String name, List<VClass> args, SourcePoint point) throws MethodNotFoundException {
        final List<VMethod> methods = Collections.synchronizedList(new ArrayList<>());
        self.methods.parallelStream().forEach(v -> {
            if (!name.equals(v.getRunTimeName()) && !name.equals(v.alias))
                return;

            if (!equalArgs(v, args)) {
                return;
            }
            methods.add(v);
        });

        self.implementsList.parallelStream().forEach(c -> {
            getMethodForSearch(c, name, methods, args);
        });

        if (self.extendsClass != null) {
            getMethodForSearch(self.extendsClass, name, methods, args);
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

        throw new MethodNotFoundException(self, name, args, point);
    }

    private static void getMethodForSearch(VClass self, String name, Collection<VMethod> out, List<VClass> args) {

        METHODS:
        for (VMethod v : self.methods) {
            if (!name.equals(v.getRunTimeName()) && !name.equals(v.alias)) {
                continue;
            }

            if (!equalArgs(v, args)) {
                continue;
            }

            if (out.contains(v)) {
                continue;
            }

            for (VMethod m : v.getReplaced()) {
                if (out.contains(out)) {
                    continue METHODS;
                }
            }


            if (out.contains(v.getReplace())) {
                out.remove(v.getReplace());
            }
            out.add(v);
        }

        if (self.extendsClass != null)
            getMethodForSearch(self.extendsClass, name, out, args);

        for (VClass v : self.implementsList)
            getMethodForSearch(v, name, out, args);
    }

    private static boolean equalArgs(VExecute exe, List<VClass> args) {
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

    public static VMethod getMethod(VClass self, String name, SourcePoint point, VClass... args) throws MethodNotFoundException {
        return getMethod(self, name, Arrays.asList(args), point);
    }

    public static VMethod getMethod(VClass self, Symbol.MethodSymbol symbol, SourcePoint point) throws MethodNotFoundException {
        Objects.requireNonNull(symbol, "Argument symbol is NULL");
        try {
            return getMethod(self, symbol.name.toString(), getMethodArgs(self, symbol, point), point);
        } catch (VClassNotFoundException e) {
            throw new MethodNotFoundException(symbol, point);
        }
    }

    private static List<VClass> getMethodArgs(VClass self, Symbol.MethodSymbol symbol, SourcePoint point) throws VClassNotFoundException {
        List<VClass> args = new ArrayList<>();

        if (symbol.name.toString().equals("<init>"))
            self.getDependencyParent().ifPresent(e -> args.add(e));

        if (symbol.params != null) {
            for (Symbol.VarSymbol e : symbol.params) {
                args.add(TypeUtil.loadClass(self.getClassLoader(), e.type, point));
            }
        } else if (symbol.erasure_field != null) {
            Type.MethodType mt = (Type.MethodType) symbol.erasure_field;
            for (Type t : mt.argtypes) {
                args.add(TypeUtil.loadClass(self.getClassLoader(), t, point));
            }
        } else if (symbol.type != null && symbol.type instanceof Type.ForAll) {
            Type.ForAll fa = (Type.ForAll) symbol.type;
            for (Type t : fa.qtype.getParameterTypes()) {
                args.add(TypeUtil.loadClass(self.getClassLoader(), t, point));
            }
        } else if (symbol.type != null && symbol.type instanceof Type.MethodType) {
            Type.MethodType fa = (Type.MethodType) symbol.type;
            for (Type t : fa.argtypes) {
                args.add(TypeUtil.loadClass(self.getClassLoader(), t, point));
            }
        }
        return args;
    }

    public static VConstructor getConstructor(VClass self, SourcePoint point, VClass... args) throws MethodNotFoundException {
        return getConstructor(self, Arrays.asList(args), point);
    }

    public static VConstructor getConstructor(VClass self, List<VClass> args, SourcePoint point) throws MethodNotFoundException {
        for (VConstructor v : self.constructors)
            if (equalArgs(v, args)) {
                return v;
            }

        throw new MethodNotFoundException(self, "<init>", args, point);
    }

    public static VConstructor getConstructor(VClass self, Symbol.MethodSymbol symbol, SourcePoint point) throws MethodNotFoundException {
        try {
            return getConstructor(self, getMethodArgs(self, symbol, point), point);
        } catch (VClassNotFoundException e) {
            throw new MethodNotFoundException(symbol, point);
        }
    }
}
