package org.tlsys.twt.compiler;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.TypeUtil;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.CompileException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class StatementCompiler {

    private static final Map<Class, ProcSt> stProc = new HashMap<>();

    static {
        addProcSt(JCTree.JCBlock.class, (c, e, o) -> {
            VBlock b = new VBlock(o);
            for (JCTree.JCStatement t : e.getStatements())
                b.operations.add(c.st(t, b));
            return b;
        });

        addProcSt(JCTree.JCExpressionStatement.class, (c, e, o) -> {
            return c.op(e.expr, o);
        });

        addProcSt(JCTree.JCReturn.class, (c, e, o) -> {
            if (e.expr == null)
                return new Return(null);
            return new Return((Value) c.op(e.expr, o));
        });

        addProcSt(JCTree.JCIf.class, (c, e, o) -> {
            VBlock thenBlock = null;
            VBlock elseBlock = null;
            VIf i = new VIf((Value) c.op(e.cond, o), o);
            if (e.thenpart != null) {
                Operation oo = c.st(e.thenpart, i);
                if (oo instanceof VBlock) {
                    i.thenBlock = (VBlock) oo;
                } else {
                    VBlock b = new VBlock(i);
                    b.operations.add(oo);
                    i.thenBlock = b;
                }

            }

            if (e.elsepart != null) {
                Operation oo = c.st(e.elsepart, i);
                if (oo instanceof VBlock) {
                    i.elseBlock = (VBlock) oo;
                } else {
                    VBlock b = new VBlock(i);
                    b.operations.add(oo);
                    i.elseBlock = b;
                }
            }
            return i;
        });

        addProcSt(JCTree.JCThrow.class, (c, e, o) -> {
            return new Throw((Value) c.op(e.expr, o));
        });

        addProcSt(JCTree.JCVariableDecl.class, (c, e, o) -> {
            SVar var = new SVar(TypeUtil.loadClass(c.getCurrentClass().getClassLoader(), e.type), e.sym);
            DeclareVar dv = new DeclareVar(var);
            if (e.init == null)
                dv.init = OperationCompiler.getInitValueForType(var.getType());
            else
                dv.init = c.op(e.init, o);
            var.name = e.name.toString();
            return dv;
        });

        addProcSt(JCTree.JCEnhancedForLoop.class, (c, e, o) -> {
            Value v = c.op(e.expr, o);
            VClass classIterable = c.getCurrentClass().getClassLoader().loadClass(Iterable.class.getName());

            if (v.getType().isParent(classIterable)) {

                VBlock block = new VBlock(o);


                VClass classIterator = c.getCurrentClass().getClassLoader().loadClass(Iterator.class.getName());


                SVar iterator = new SVar(classIterator, null);
                DeclareVar it = new DeclareVar(iterator);
                it.init = new Invoke(v.getType().getMethod("iterator"), v);
                block.operations.add(it);
                WhileLoop wl = new WhileLoop(block);
                wl.value = new Invoke(classIterator.getMethod("hasNext"), it.getVar());
                wl.block = new VBlock(wl);
                block.operations.add(wl);

                SVar var = new SVar(TypeUtil.loadClass(c.getCurrentClass().getClassLoader(), e.var.type), e.var.sym);
                DeclareVar dv = new DeclareVar(var);
                var.name = e.var.name.toString();
                dv.init = new Invoke(classIterator.getMethod("next"), it.getVar());
                wl.block.operations.add(dv);
                wl.block.operations.add(c.st(e.body, wl.block));
                return block;
            } else {
                ArrayClass ac = (ArrayClass)v.getType();
                ForLoop forLoop = new ForLoop(o);
                forLoop.block = new VBlock(forLoop);
                VClass intClass = c.getCurrentClass().getClassLoader().loadClass("int");
                SVar itVar = new SVar(intClass, null);
                itVar.name="i" + Integer.toString(itVar.hashCode(), Character.MAX_RADIX);
                DeclareVar it = new DeclareVar(itVar);
                it.init = new Const(0, intClass);
                forLoop.init = it;
                forLoop.update = new Increment(itVar, Increment.IncType.PRE_INC, intClass);
                forLoop.value = new VBinar(itVar, new GetField(v, v.getType().getField("length")),c.getCurrentClass().getClassLoader().loadClass("boolean"), VBinar.BitType.LT);

                SVar el = new SVar(TypeUtil.loadClass(c.getCurrentClass().getClassLoader(), e.var.type), e.var.sym);
                el.name=e.var.name.toString();
                DeclareVar dv = new DeclareVar(el);
                dv.init = new ArrayGet(v, itVar);
                forLoop.block.operations.add(dv);


                if (e.body != null) {
                    forLoop.block.operations.add(c.st(e.body, forLoop.block));
                }
                return forLoop;
            }
        });

        addProcSt(JCTree.JCWhileLoop.class, (c, e, o) -> {
            Value v = c.op(e.cond, o);
            WhileLoop fe = new WhileLoop(o);
            fe.value = c.op(e.cond, o);
            Operation op = c.st(e.body, fe);
            if (!(op instanceof VBlock)) {
                VBlock b = new VBlock(fe);
                b.operations.add(op);
                op = b;
            }
            fe.block = (VBlock) op;
            return fe;
        });

        addProcSt(JCTree.JCForLoop.class, (c, e, o) -> {
            ForLoop f = new ForLoop(o);
            if (e.init != null) {
                if (e.init.size() > 1)
                    throw new RuntimeException("Not support same init value");
                if (!e.init.isEmpty())
                    f.init = c.st(e.init.get(0), f);
            }

            if (e.cond != null) {
                f.value = c.op(e.cond, f);
            }

            if (e.step != null) {
                if (e.step.size() > 1)
                    throw new RuntimeException("Not support same init value");
                if (!e.step.isEmpty())
                    f.update = c.st(e.step.get(0), f);
            }
            Operation oo = c.st(e.body, f);
            if (!(oo instanceof VBlock)) {
                VBlock b = new VBlock(f);
                b.operations.add(oo);
                oo = b;
            }
            f.block = (VBlock) oo;
            return f;
        });

        addProcSt(JCTree.JCContinue.class, (c, e, o) -> {
            Label l = null;
            if (e.label != null) {
                l = o.findLabel(e.label.toString()).get();
            }
            return new Continue(l);
        });


        addProcSt(JCTree.JCTry.class, (c, e, o) -> {
            if (e.resources != null && !e.resources.isEmpty())
                throw new RuntimeException("Try with resurce not supported yet");
            Try tr = new Try(o);
            tr.block = (VBlock) c.st(e.body, o);
            for (JCTree.JCCatch ca : e.catchers) {
                SVar var = new SVar(TypeUtil.loadClass(c.getCurrentClass().getClassLoader(), ca.param.type), ca.param.sym);
                DeclareVar dv = new DeclareVar(var);
                var.name = ca.param.name.toString();
                Try.Catch cc = new Try.Catch(tr, dv);
                if (ca.param.vartype instanceof JCTree.JCTypeUnion) {
                    JCTree.JCTypeUnion ut = (JCTree.JCTypeUnion)ca.param.vartype;
                    for (JCTree.JCExpression ee : ut.alternatives)
                        cc.classes.add(TypeUtil.loadClass(c.getCurrentClass().getClassLoader(), ee.type));
                } else
                    cc.classes.add(TypeUtil.loadClass(c.getCurrentClass().getClassLoader(), ca.param.vartype.type));
                cc.block = (VBlock) c.st(ca.body, cc);
                tr.catchs.add(cc);
            }
            return tr;
        });

        addProcSt(JCTree.JCSwitch.class, (c,e,o)->{
            Switch s = new Switch(o, c.op(e.selector, o));
            for (JCTree.JCCase cc : e.getCases()) {
                Switch.Case ca = new Switch.Case(s);
                ca.value = cc.getExpression()==null?null:c.op(cc.getExpression(), s);
                ca.block = new VBlock(ca);
                for (JCTree.JCStatement ss : cc.getStatements()) {
                    ca.block.operations.add(c.st(ss, ca.block));
                }
                s.cases.add(ca);
            }
            return s;
        });
    }

    private static <V extends JCTree.JCStatement> void addProcSt(Class<V> cl, ProcSt<V> proc) {
        stProc.put(cl, proc);
    }

    public static Operation st(TreeCompiler compiler, JCTree.JCStatement sta, Context context) throws CompileException {
        ProcSt p = stProc.get(sta.getClass());
        if (p != null)
            return p.proc(compiler, sta, context);
        throw new RuntimeException("Not supported " + sta.getClass().getName() + " \"" + sta + "\"");
    }

    private static interface ProcSt<V extends JCTree.JCStatement> {
        Operation proc(TreeCompiler compiller, V e, Context context) throws CompileException;
    }

    public static VMethod createBrig(VMethod from, VMethod to) {
        VMethod rep = new VMethod(to.getParent(), null, null);
        rep.setReplace(from);
        rep.arguments.addAll(from.arguments);
        rep.block = new VBlock(rep);
        rep.alias = from.alias;

        Invoke inv = new Invoke(to, new This(to.getParent()));
        inv.arguments.addAll(rep.arguments);
        inv.returnType = to.returnType;

        if (!(from.returnType instanceof ArrayClass) && from.returnType.isThis("void")) {
            rep.block.add(inv);
        } else {
            rep.block.add(new Return(inv));
        }
        return rep;
    }

}
