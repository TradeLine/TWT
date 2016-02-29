package org.tlsys.twt.compiler;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import org.tlsys.TypeUtil;
import org.tlsys.lex.Const;
import org.tlsys.lex.Invoke;
import org.tlsys.lex.NewClass;
import org.tlsys.lex.VVar;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.DomNode;
import org.tlsys.twt.annotations.ReplaceClass;

import java.util.*;
import org.tlsys.twt.annotations.ForceInject;

public class ClassCompiler {

    /**
     * Компилирует массив файлов, указывая в качестве родительского загрузчика классов {@param classLoader}
     *
     * @param classes     массив файлов
     * @param classLoader загрузчик, для найденых классов
     * @param listener    сообщает когда находится класс
     */
    public static void compile(Collection<CompilationUnitTree> classes, VClassLoader classLoader, ClassItemListener listener) throws CompileException {
        CompileContext cc = new CompileContext(classLoader);

        for (CompilationUnitTree cu : classes) {
            for (Tree tt : cu.getTypeDecls()) {
                if (tt instanceof JCTree.JCClassDecl) {
                    compileClassDefine(null, (JCTree.JCClassDecl) tt, cc, listener);
                }
            }
        }

        setExtends(cc);
        searchMembers(cc);

        compileCode(cc, VExecute.class);
        compileCode(cc, VVar.class);
        compileCode(cc, StaticBlock.class);
        findReplaceMethod(cc);
    }

    public static AnnonimusClass createAnnonimusClass(JCTree.JCClassDecl c, VClassLoader vClassLoader) throws CompileException {
        AnnonimusClass as = new AnnonimusClass(null, c.sym);
        as.setClassLoader(vClassLoader);
        Pair p = new Pair(as, c);
        setExtends(p, vClassLoader);
        searchMembers(p);
        compileCode(p, VExecute.class);
        compileCode(p, VVar.class);
        findReplaceMethod(p);
        return as;
    }

    private static void compileClassDefine(VClass parent, JCTree.JCClassDecl des, CompileContext context, ClassItemListener listener) {
        VClass v = createClassFromDes(parent, des, context.getLoader());
        context.addPair(des, v);
        if (listener != null)
            listener.doneClass(v);

        for (JCTree t : des.defs) {
            if (t instanceof JCTree.JCClassDecl) {
                compileClassDefine(v, (JCTree.JCClassDecl) t, context, listener);
            }
        }
    }

    private static VClass createClassFromDes(VClass parent, JCTree.JCClassDecl c, VClassLoader vClassLoader) {
        VClass v = new VClass(parent, c.sym);
        v.name = c.name.toString();
        v.realName = c.sym.toString();
        v.fullName = v.realName;
        v.force = CompilerTools.isAnnatationExist(c.getModifiers(), ForceInject.class);
        v.setModificators(CompilerTools.toFlags(c.getModifiers().getFlags()));
        CompilerTools.getAnnatationValueClass(c.getModifiers(), CodeGenerator.class).ifPresent(e -> v.codeGenerator = e);
        CompilerTools.getAnnatationValueString(c.getModifiers(), ClassName.class).ifPresent(e -> v.alias = e);
        CompilerTools.getAnnatationValueClass(c.getModifiers(), ReplaceClass.class).ifPresent(e -> v.alias = e);
        CompilerTools.getAnnatationValueString(c.getModifiers(), DomNode.class).ifPresent(e -> v.domNode = e);
        v.setClassLoader(vClassLoader);
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
            throw new CompileException("Error compile " + pair.vclass.realName, e);
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
            pair.vclass.extendsClass = loader.loadClass(ex.type.tsym.toString());
        } else {
            Type.ClassType ct = (Type.ClassType) pair.desl.type;

            pair.vclass.extendsClass = TypeUtil.loadClass(loader, ct.supertype_field);
            //p.vclass.extendsClass = loader.loadClass(java.lang.Object.class.getName());
        }
        if (pair.vclass.alias != null && pair.vclass.alias.equals(java.lang.Object.class.getName()))
            pair.vclass.extendsClass = null;
        for (JCTree.JCExpression e : pair.desl.implementing) {
            pair.vclass.implementsList.add(loader.loadClass(e.type.tsym.toString()));
        }
    }

    private static void setExtends(CompileContext ctx) throws VClassNotFoundException {
        for (Pair p : ctx.pairs) {
            setExtends(p, ctx.getLoader());
        }
    }

    public static void compileCode(Pair p, Class forClass) throws CompileException {
        TreeCompiler com = new TreeCompiler(p.vclass);
        for (Map.Entry<JCTree, Member> e : p.members.entrySet()) {
            Member member = e.getValue();
            if (!forClass.isInstance(member))
                continue;
            JCTree tree = e.getKey();

            if (tree instanceof JCTree.JCVariableDecl) {
                JCTree.JCVariableDecl v = (JCTree.JCVariableDecl) tree;
                VField f = (VField) member;
                
                if (v.init == null) {
                    if (!java.lang.reflect.Modifier.isStatic(f.getModificators()))
                        f.init = OperationCompiler.getInitValueForType(f.getType());
                } else {
                    f.init = com.op(v.init, f.getParent());
                    VClass enumClass = f.getParent().getClassLoader().loadClass(Enum.class.getName());
                    if (f.getParent() != enumClass && f.getParent().isParent(enumClass)) {
                        NewClass nc = (NewClass) f.init;
                        nc.addArg(new Const(f.alias != null ? f.alias : f.name, f.getParent().getClassLoader().loadClass(String.class.getName())));
                        nc.addArg(new Const(f.getParent().fields.indexOf(f), f.getParent().getClassLoader().loadClass("int")));
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
    }

    public static void compileCode(CompileContext ctx, Class forClass) throws CompileException {
        for (Pair p : ctx.pairs) {
            compileCode(p, forClass);
        }
    }

    private static void compileExecuteCode(TreeCompiler com, VExecute method, JCTree.JCMethodDecl dec) throws CompileException {
        if (dec.body == null)
            return;

        try {
            method.block = (VBlock) com.st(dec.body, method);
            if (method instanceof VConstructor) {
                VConstructor cons = (VConstructor) method;
                if (!method.block.operations.isEmpty()) {
                    if (method.block.operations.get(0) instanceof Invoke) {
                        Invoke inv = (Invoke) method.block.operations.get(0);
                        if (inv.getMethod() instanceof VConstructor) {
                            cons.parentConstructorInvoke = inv;
                            cons.block.operations.remove(0);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            throw new CompileException("Can't compile " + method.getParent().fullName + "::" + method.getRunTimeName(), e);
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

            if (m.arguments.size() != member.arguments.size())
                continue;

            for (VArgument a : m.arguments) {
                for (VArgument b : member.arguments) {
                    if (a.getType() != b.getType())
                        continue METHOD;
                }
            }

            member.setReplace(m);
            break;
        }

        METHOD:
        for (VMethod m : methods) {
            if (m == member)
                continue;

            if (m.arguments.isEmpty())
                continue;
            if (m.arguments.size() != member.arguments.size())
                continue;

            for (VArgument a : m.arguments) {
                for (VArgument b : member.arguments) {
                    if (a.generic) {
                        if (!b.getType().isParent(a.getType()))
                            continue METHOD;
                    } else// if (a.getType() != b.getType())
                        continue METHOD;
                }
            }
            member.getParent().methods.add(StatementCompiler.createBrig(m, member));
            break;
        }
    }


    public interface ClassItemListener {
        public void doneClass(VClass vClass);
    }

    private static class CompileContext {
        private final VClassLoader loader;
        private final Set<Pair> pairs = new HashSet<>();

        private CompileContext(VClassLoader loader) {
            this.loader = loader;
        }

        public void addPair(JCTree.JCClassDecl dess, VClass clazz) {
            pairs.add(new Pair(clazz, dess));
        }

        public VClassLoader getLoader() {
            return loader;
        }
    }

    private static class Pair {
        public final Map<JCTree, Member> members = new HashMap<>();
        public VClass vclass;
        public JCTree.JCClassDecl desl;

        public Pair(VClass vclass, JCTree.JCClassDecl desl) {
            this.vclass = vclass;
            this.desl = desl;
        }
    }
}
