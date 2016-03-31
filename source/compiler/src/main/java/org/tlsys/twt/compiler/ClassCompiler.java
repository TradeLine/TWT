package org.tlsys.twt.compiler;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import org.tlsys.InputsClassModificator;
import org.tlsys.OtherClassLink;
import org.tlsys.TypeUtil;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;
import org.tlsys.sourcemap.SourceFile;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.annotations.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

public class ClassCompiler {

    private static Function<VExecute, Void> parentThisReplacer = as -> {
        BoolRef b = new BoolRef(true);
        while (b.isValue()) {
            b.setValue(false);

            as.visit((r) -> {
                VClass currentClass = as.getParent();
                if (r.get() instanceof This) {
                    This t = (This) r.get();
                    if (t.getType() != as.getParent()) {
                        OtherClassLink ocl = OtherClassLink.getOrCreate(as.getParent(), t.getType());

                        r.set(new GetField(new This(as.getParent()), ocl.getField(), null));
                        b.setValue(true);
                    }
                    return false;
                }
                return true;
            });
        }

        as.visit(r -> {
            if (r.get() instanceof SVar) {

                SVar s = (SVar) r.get();
                Optional<Context> ctx = TypeUtil.findParentContext(s, c -> {
                    if (c == as.getParent())
                        return true;
                    return false;
                });

                if (!ctx.isPresent()) {
                    VField f = InputsClassModificator.getOrCreateInputModificator(as.getParent()).addInput(s);
                    r.set(new GetField(new This(as.getParent()), f, null));
                }

                return false;
            }

            return true;
        });
        return null;
    };

    /**
     * Компилирует массив файлов, указывая в качестве родительского загрузчика классов {@param classLoader}
     *
     * @param classes     массив файлов
     * @param classLoader загрузчик, для найденых классов
     * @param listener    сообщает когда находится класс
     */
    public static void compile(List<CompilationUnitTree> classes, VClassLoader classLoader, ClassItemListener listener) throws CompileException {
        CompileContext cc = new CompileContext(classLoader);

        for (CompilationUnitTree cu : classes) {
            cc.addSourceFile(cu);
        }

        ENUM_SEARCH:
        for (CompilationUnitTree cu : classes) {
            for (Tree tt : cu.getTypeDecls()) {
                if (tt instanceof JCTree.JCClassDecl) {
                    JCTree.JCClassDecl cl = (JCTree.JCClassDecl) tt;
                    Optional<String> st = CompilerTools.getAnnatationValueClass(cl.getModifiers(), ReplaceClass.class);
                    if (st.isPresent() && st.get().equals(Enum.class.getName())) {
                        for (Tree tt2 : cu.getTypeDecls()) {
                            compileClassDefine(null, (JCTree.JCClassDecl) tt2, cc, listener, cu);
                        }
                        classes.remove(cu);
                        break ENUM_SEARCH;

                    }
                }
            }
        }

        for (CompilationUnitTree cu : classes) {
            for (Tree tt : cu.getTypeDecls()) {
                if (tt instanceof JCTree.JCClassDecl) {
                    compileClassDefine(null, (JCTree.JCClassDecl) tt, cc, listener, cu);
                }
            }
        }

        setExtends(cc);
        searchMembers(cc);


        for (Pair p : cc.pairs) {
//            parentThisReplacer.apply(p.vclass);

            if (p.vclass.getDependencyParent().isPresent()) {

                OtherClassLink.getOrCreate(p.vclass, p.vclass.getDependencyParent().get());

                //p.vclass.addMod(new ParentClassModificator(p.vclass));
            }
        }

        compileCode(cc, VExecute.class);


        compileCode(cc, VVar.class);
        compileCode(cc, StaticBlock.class);
        findReplaceMethod(cc);
    }

    public static AnnonimusClass createAnnonimusClass(CompileContext ctx, Context context, JCTree.JCClassDecl c, VClassLoader vClassLoader) throws CompileException {
        AnnonimusClass as = new AnnonimusClass(context, null, c.sym);

        VClass parentClazz = vClassLoader.loadClass(AnnonimusClass.extractParentClassName(c.sym));
        as.setParentContext(parentClazz);
        as.fullName = as.getRealName();
        as.setParentContext(context);

        parentClazz.addChild(as);
        vClassLoader.classes.add(as);

        as.setClassLoader(vClassLoader);



        Pair p = new Pair(as, c, ctx.getPairByClass((VClass) TypeUtil.findParentContext(context, e->e instanceof VClass).get()).file);
        setExtends(p, vClassLoader);
        searchMembers(p);
        compileCode(ctx, p, VExecute.class);
        compileCode(ctx, p, VVar.class);
        findReplaceMethod(p);

        //as.setModificators(as.getModificators());

        //заменяет все this родителя на проброшеную переменную
        for (VConstructor ee : as.constructors)
            parentThisReplacer.apply(ee);

        for (VMethod ee : as.methods)
            parentThisReplacer.apply(ee);

        /*
        as.visit(r->{
            if (r.get() instanceof SVar) {
                SVar v = (SVar)r.get();
                Optional<Context> ctx = TypeUtil.findParentContext(v, e->e != as);
            }
            return true;
        });
        */


        /*
        as.visit((r)->{
            if (r.get() instanceof GetField) {
                GetField gf = (GetField)r.get();
                if (gf.getField().isStatic())
                    return false;

                OtherClassLink ocl = OtherClassLink.getOrCreate(as, gf.getField().getParent());

                r.set(new GetField(ocl.getField(), gf.getField()));
                return false;
            }

            if (r.get() instanceof SetField) {
                SetField gf = (SetField)r.get();
                if (gf.getField().isStatic())
                    return true;

                OtherClassLink ocl = OtherClassLink.getOrCreate(as, gf.getField().getParent());

                r.set(new SetField(ocl.getField(), gf.getField(), gf.getValue(), gf.getOpType()));
                return true;
            }

            return false;
        });
        */

        /*
        VPackage pac = (VPackage) TypeUtil.findParentContext(as.extendsClass, e->e instanceof VPackage).get();
        as.setParentContext(pac);
        */


        as.setParentContext(parentClazz);


        return as;
    }

    private static void compileClassDefine(VClass parent, JCTree.JCClassDecl des, CompileContext context, ClassItemListener listener, CompilationUnitTree file) throws VClassNotFoundException {
        VClass v = createClassFromDes(parent, des, context.getLoader());
        context.addPair(des, v, file);
        if (listener != null)
            listener.doneClass(v);

        for (JCTree t : des.defs) {
            if (t instanceof JCTree.JCClassDecl) {
                compileClassDefine(v, (JCTree.JCClassDecl) t, context, listener, file);
            }
        }
    }

    private static VClass createClassFromDes(VClass parent, JCTree.JCClassDecl c, VClassLoader vClassLoader) throws VClassNotFoundException {

        String[] list = c.sym.toString().split("\\.");


        Context parentContext = null;
        if (parent != null) {
            parentContext = parent;
        } else {
            if (list.length == 1) {
                parentContext = vClassLoader.getRootPackage();
            } else {
                VPackage p = vClassLoader.getRootPackage();
                for (int i = 0; i < list.length - 1; i++) {
                    Optional<VPackage> v = p.getPackage(list[i]);
                    if (v.isPresent()) {
                        p = v.get();
                    } else {
                        p = new VPackage(list[i], p);
                    }
                }
                parentContext = p;
            }
        }

        if (parentContext == null)
            throw new RuntimeException("Can't find parent for class " + c.sym.toString());

        VClass v = new VClass(list[list.length - 1], parentContext, parent, c.sym);

        if (parentContext instanceof VPackage) {
            ((VPackage) parentContext).addChild(v);
        } else if (parentContext instanceof VClass) {
            ((VClass) parentContext).addChild(v);
        } else {
            throw new RuntimeException("Unknown parent " + parentContext);
        }

        v.name = c.name.toString();
        //v.realName = c.sym.toString();
        v.fullName = v.getRealName();
        v.force = CompilerTools.isAnnatationExist(c.getModifiers(), ForceInject.class);
        v.setModificators(CompilerTools.toFlags(c.getModifiers()));
        v.setClassLoader(vClassLoader);

        CompilerTools.getAnnatationValueString(c.getModifiers(), ClassName.class).ifPresent(e -> v.alias = e);
        CompilerTools.getAnnatationValueClass(c.getModifiers(), ReplaceClass.class).ifPresent(e -> v.alias = e);
        CompilerTools.getAnnatationValueClass(c.getModifiers(), CastAdapter.class).ifPresent(e -> v.castGenerator = e);


        /*
        if (!Enum.class.getName().equals(v.alias))
            if (v.getDependencyParent(vClassLoader.loadClass(Enum.class.getName())).isPresent()) {//если класс имеет жетскую привязку к родителю
                System.out.println("CHILD " + v + " <=" + v.getDependencyParent().get() + " - " + v.hashCode());
                TypeUtil.createParentThis(v);//то создаем this на родителя
            }
            */

        CompilerTools.getAnnatationValueClass(c.getModifiers(), CodeGenerator.class).ifPresent(e -> v.codeGenerator = e);

        CompilerTools.getAnnatationValueString(c.getModifiers(), DomNode.class).ifPresent(e -> v.domNode = e);
        return v;
    }

    private static void searchMembers(Pair pair) throws CompileException {
        try {
            for (JCTree t : pair.desl.defs) {
                Member m = CompilerTools.createMember(pair.vclass, t);
                if (m != null)
                    pair.members.put(t, m);
            }
        } catch (VClassNotFoundException e) {
            throw new CompileException("Error compile " + pair.vclass.getRealName(), e);
        }
    }

    private static void searchMembers(CompileContext ctx) throws CompileException {
        for (Pair p : ctx.pairs) {
            searchMembers(p);
        }
    }

    private static void setExtends(Pair pair, VClassLoader loader) throws VClassNotFoundException {
        JCTree.JCExpression ex = pair.desl.getExtendsClause();
        if (ex != null) {
            pair.vclass.extendsClass = TypeUtil.loadClass(loader, ex.type);
        } else {
            Type.ClassType ct = (Type.ClassType) pair.desl.type;

            pair.vclass.extendsClass = TypeUtil.loadClass(loader, ct.supertype_field);
            //p.vclass.extendsClass = loader.loadClass(java.lang.Object.class.getName());
        }
        if (pair.vclass.alias != null && pair.vclass.alias.equals(java.lang.Object.class.getName()))
            pair.vclass.extendsClass = null;
        for (JCTree.JCExpression e : pair.desl.implementing) {
            /*
            if (e.type.tsym.toString().contains("RenderPass") || e.type.tsym.toString().contains("RenderComponent"))
                System.out.println("123");
                */
            VClass cl = TypeUtil.loadClass(loader, e.type);
            pair.vclass.implementsList.add(cl);
        }
    }

    private static void setExtends(CompileContext ctx) throws VClassNotFoundException {
        for (Pair p : ctx.pairs) {
            setExtends(p, ctx.getLoader());
        }
    }

    public static void compileCode(CompileContext ctx, Pair p, Class forClass) throws CompileException {

        TreeCompiler com = new TreeCompiler(p.vclass, ctx.getFileSource(p.file), ctx);
        for (Map.Entry<JCTree, Member> e : p.members.entrySet()) {
            Member member = e.getValue();
            if (!forClass.isInstance(member))
                continue;
            JCTree tree = e.getKey();

            if (tree instanceof JCTree.JCVariableDecl) {
                JCTree.JCVariableDecl v = (JCTree.JCVariableDecl) tree;
                VField f = (VField) member;

                if (v.init == null) {
                    if (!java.lang.reflect.Modifier.isFinal(f.getModificators()))
                        f.init = OperationCompiler.getInitValueForType(f.getType());
                    else
                        f.init = null;
                } else {
                    f.init = com.op(v.init, f.getParent());
                    VClass enumClass = f.getParent().getClassLoader().loadClass(Enum.class.getName());
                    if (f.getParent() != enumClass && f.getParent().isParent(enumClass)) {
                        NewClass nc = (NewClass) f.init;
                        nc.addArg(new Const(f.getAliasName() != null ? f.getAliasName() : f.getRealName(), f.getParent().getClassLoader().loadClass(String.class.getName())));
                        nc.addArg(new Const(f.getParent().getLocalFields().indexOf(f), f.getParent().getClassLoader().loadClass("int")));
                    }
                }
                continue;
            }

            if (member instanceof VExecute) {
                compileExecuteCode(com, (VExecute) member, (JCTree.JCMethodDecl) tree);
                continue;
            }

            if (member instanceof StaticBlock) {
                StaticBlock sb = (StaticBlock) member;
                JCTree.JCBlock b = (JCTree.JCBlock) tree;
                for (JCTree.JCStatement t : b.getStatements()) {
                    sb.getBlock().add(com.st(t, sb));
                }
                continue;
            }

            throw new RuntimeException("Code analize for " + tree.getClass().getName() + " not ready yet");
        }

        if (forClass == VExecute.class) {
            for (Map.Entry<JCTree, Member> e : p.members.entrySet()) {
                if (e.getValue() instanceof VExecute) {
                    parentThisReplacer.apply((VExecute) e.getValue());
                }
            }
        }
    }

    public static void compileCode(CompileContext ctx, Class forClass) throws CompileException {
        for (Pair p : ctx.pairs) {
            compileCode(ctx, p, forClass);
        }
    }

    private static void compileExecuteCode(TreeCompiler com, VExecute method, JCTree.JCMethodDecl dec) throws CompileException {
        if (dec.body == null)
            return;

        try {
            method.setBlock((VBlock) com.st(dec.body, method));
            if (method instanceof VConstructor) {
                VConstructor cons = (VConstructor) method;
                if (!method.getBlock().getNativeOperations().isEmpty()) {
                    if (method.getBlock().getNativeOperations().get(0) instanceof Invoke) {
                        Invoke inv = (Invoke) method.getBlock().getNativeOperations().get(0);
                        if (inv.getMethod() instanceof VConstructor) {
                            cons.parentConstructorInvoke = inv;
                            cons.getBlock().getNativeOperations().remove(0);
                        }
                    }
                    /*
                    if (method.getParent().getDependencyParent().isPresent()) {//если класс имеет жесткую привязку к родителю
                        SetField sf = new SetField(new This(method.getParent()), TypeUtil.getParentThis(method.getParent()), cons.getArguments().get(0), Assign.AsType.ASSIGN);//то формируем присваение аргумента (this родителя) в локальную переменную
                        method.block.getNativeOperations().add(0, sf);
                    }
                    */
                }
            }
        } catch (Throwable e) {
            throw new CompileException("Can't compile " + method.getParent().getRealName() + "::" + method.getRunTimeName(), e);
        }
    }

    private static void findReplaceMethod(Pair p) {
        for (Member m : p.members.values())
            if (m instanceof VMethod)
                findReplaceMethodInClass((VMethod) m);
    }

    private static void findReplaceMethod(CompileContext ctx) {
        for (Pair p : ctx.pairs) {
            findReplaceMethod(p);
        }
    }

    private static List<VMethod> getAllMethodsNyName(VClass clazz, String name) {
        ArrayList<VMethod> out = new ArrayList<>();
        for (VMethod m : clazz.methods) {
            if (m.isThis(name))
                out.add(m);
        }

        if (clazz.extendsClass != null)
            out.addAll(getAllMethodsNyName(clazz.extendsClass, name));

        for (VClass c : clazz.implementsList) {
            out.addAll(getAllMethodsNyName(c, name));
        }
        return out;
    }

    private static void findReplaceMethodInClass(VMethod member) {

        List<VMethod> methods = getAllMethodsNyName(member.getParent(), member.getRunTimeName());
        METHOD:
        for (VMethod m : methods) {
            if (m == member)
                continue;

            if (m.getArguments().size() != member.getArguments().size()) {
                continue;
            }

            for (int i = 0; i < m.getArguments().size(); i++) {
                //for (VArgument b : member.arguments) {
                if (m.getArguments().get(i).getType() != member.getArguments().get(i).getType()) {
                    continue METHOD;
                }
                //}
            }

            member.setReplace(m);
            break;
        }

        METHOD:
        for (VMethod m : methods) {
            if (m == member)
                continue;
            if (m.getParent() == member.getParent())
                continue;

            if (m.getArguments().isEmpty()) {
                continue;
            }
            if (m.getArguments().size() != member.getArguments().size()) {
                continue;
            }

            for (VArgument a : m.getArguments()) {
                for (VArgument b : member.getArguments()) {
                    if (a.generic) {
                        if (!b.getType().isParent(a.getType())) {
                            continue METHOD;
                        }
                    } else {
                        continue METHOD;
                    }
                }
            }
            member.getParent().methods.add(StatementCompiler.createBrig(m, member));
            break;
        }
    }

    public interface ClassItemListener {
        public void doneClass(VClass vClass);
    }

    private static class BoolRef {
        private boolean value;

        public BoolRef() {
            this(false);
        }

        public BoolRef(boolean value) {
            this.value = value;
        }

        public boolean isValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }

    public static class CompileContext {
        private final VClassLoader loader;
        private final Set<Pair> pairs = new HashSet<>();
        private final Map<CompilationUnitTree, SourceFile> files = new HashMap<>();

        private CompileContext(VClassLoader loader) {
            this.loader = loader;
        }

        public void addPair(JCTree.JCClassDecl dess, VClass clazz, CompilationUnitTree file) {
            pairs.add(new Pair(clazz, dess, file));
        }

        public Pair getPairByClass(VClass clazz) {
            for (Pair p : pairs) {
                if (p.vclass== clazz)
                    return p;
            }

            throw new RuntimeException("Can't find pair for class " + clazz.getRealName());
        }

        public void addSourceFile(CompilationUnitTree file) {
            try (InputStream is = file.getSourceFile().openInputStream()) {
                byte[] buffer = new byte[512];
                int len;
                ByteArrayOutputStream data = new ByteArrayOutputStream();
                while ((len=is.read(buffer))!=-1) {
                    data.write(buffer, 0, len);
                }
                files.put(file, new SourceFile(new String(data.toByteArray()), file.getSourceFile().getName()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public SourceFile getFileSource(CompilationUnitTree file) {
            SourceFile sf = files.get(file);
            if (sf == null)
                throw new RuntimeException("Can't find source for " + file.getSourceFile().toUri());
            return sf;
        }

        public VClassLoader getLoader() {
            return loader;
        }
    }

    private static class Pair {
        public final Map<JCTree, Member> members = new HashMap<>();
        public VClass vclass;
        public JCTree.JCClassDecl desl;
        public CompilationUnitTree file;

        public Pair(VClass vclass, JCTree.JCClassDecl desl, CompilationUnitTree file) {
            this.vclass = vclass;
            this.desl = desl;
            this.file = file;
        }
    }
}
