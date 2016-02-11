package org.tlsys.twt.compiler;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import org.tlsys.TypeUtil;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.DomNode;
import org.tlsys.twt.annotations.ReplaceClass;

import java.util.*;

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
        CompilerTools.getAnnatationValueClass(c.getModifiers(), CodeGenerator.class).ifPresent(e -> v.codeGenerator = e);
        CompilerTools.getAnnatationValueClass(c.getModifiers(), ReplaceClass.class).ifPresent(e -> v.alias = e);
        CompilerTools.getAnnatationValueString(c.getModifiers(), DomNode.class).ifPresent(e -> v.domNode = e);
        v.setClassLoader(vClassLoader);
        return v;
    }

    private static void searchMembers(CompileContext ctx) throws CompileException {
        for (Pair p : ctx.pairs) {
            try {
                for (JCTree t : p.desl.defs) {
                    Member m = CompilerTools.createMember(p.vclass, t);
                    if (m != null)
                        p.members.put(t, m);
                }
            } catch (VClassNotFoundException e) {
                throw new CompileException("Error compile " + p.vclass.realName, e);
            }
        }
    }

    private static void setExtends(CompileContext ctx) throws VClassNotFoundException {
        for (Pair p : ctx.pairs) {
            JCTree.JCExpression ex = p.desl.getExtendsClause();
            if (ex != null) {
                p.vclass.extendsClass = ctx.getLoader().loadClass(ex.type.tsym.toString());
            } else {
                Type.ClassType ct = (Type.ClassType) p.desl.type;

                p.vclass.extendsClass = TypeUtil.loadClass(ctx.getLoader(), ct.supertype_field);
                //p.vclass.extendsClass = loader.loadClass(java.lang.Object.class.getName());
            }
            if (p.vclass.alias != null && p.vclass.alias.equals(java.lang.Object.class.getName()))
                p.vclass.extendsClass = null;
            for (JCTree.JCExpression e : p.desl.implementing) {
                p.vclass.implementsList.add(ctx.getLoader().loadClass(e.type.tsym.toString()));
            }
        }
    }

    public static void compileCode(Compiller com, Member member, JCTree tree, VClassLoader loader) throws CompileException {
        if (tree instanceof JCTree.JCVariableDecl) {
            JCTree.JCVariableDecl v = (JCTree.JCVariableDecl) tree;
            VField f = (VField) member;
            if (v.init == null)
                f.init = com.init(f.getType());
            else {
                f.init = com.op(v.init, f.getParent());
                VClass enumClass = f.getParent().getClassLoader().loadClass(Enum.class.getName());
                if (f.getParent() != enumClass && f.getParent().isParent(enumClass)) {
                    NewClass nc = (NewClass) f.init;
                    nc.addArg(new Const(f.alias != null ? f.alias : f.name, f.getParent().getClassLoader().loadClass(String.class.getName())));
                    nc.addArg(new Const(f.getParent().fields.indexOf(f), f.getParent().getClassLoader().loadClass("int")));
                }
            }
            return;
        }

        if (member instanceof VExecute) {
            com.exeCode((VExecute) member, (JCTree.JCMethodDecl) tree);
            return;
        }

        if (member instanceof StaticBlock) {
            StaticBlock sb = (StaticBlock)member;
            JCTree.JCBlock b = (JCTree.JCBlock)tree;
            for (JCTree.JCStatement t : b.getStatements()) {
                sb.getBlock().add(com.st(t,sb));
            }
            return;
        }

        throw new RuntimeException("Code analize for " + tree.getClass().getName() + " not ready yet");
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

    public interface ClassItemListener {
        public void doneClass(VClass vClass);
    }
}
