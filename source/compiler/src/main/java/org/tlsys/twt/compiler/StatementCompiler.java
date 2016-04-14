package org.tlsys.twt.compiler;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.CodeBuilder;
import org.tlsys.TypeUtil;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;
import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.CompileException;

import java.util.*;

class StatementCompiler {

    private static final Map<Class, ProcSt> stProc = new HashMap<>();

    static {
        addProcSt(JCTree.JCBlock.class, (c, e, o) -> {
            VBlock b = new VBlock(o, e.pos < 0 ? null : c.getFile().getPoint(e.pos), e.endpos < 0 ? null : c.getFile().getPoint(e.endpos));
            int lastLine = -1;
            for (JCTree.JCStatement t : e.getStatements()) {
                Operation oo = c.st(t, b);
                if (o != null) {
                    SourcePoint start = e.pos < 0 ? null : c.getFile().getPoint(e.pos);
                    if (start.getRow() > lastLine) {
                        lastLine = start.getRow();
                        start = c.getFile().getPoint(start.getRow(), 0);
                    }
                    b.add(new Line(oo, start, e.endpos < 0 ? null : c.getFile().getPoint(e.endpos), b));
                }
                //b.add(oo);
            }
            return b;
        });

        addProcSt(JCTree.JCExpressionStatement.class, (c, e, o) -> {
            return c.op(e.expr, o);
        });

        addProcSt(JCTree.JCReturn.class, (c, e, o) -> {
            if (e.expr == null)
                return new Return(null, c.getFile().getPoint(e.pos));

            Optional<Context> ctx = TypeUtil.findParentContext(o, ee->ee instanceof VExecute || ee instanceof Lambda);

            if (!ctx.isPresent())
                throw new RuntimeException("Can't find root content for get return type");

            VClass needClass = null;

            if (ctx.get() instanceof Lambda) {
                needClass = ((Lambda)ctx.get()).getMethod().returnType;
            } else if (ctx.get() instanceof VExecute) {
                needClass = ((VExecute)ctx.get()).returnType;
            }

            Objects.requireNonNull(needClass, "Need Class for return is NULL");

            Value v = c.op(e.expr, o);
            if (v == null) {
                throw new NullPointerException("VALUE is NULL: " + e.expr);
            }
            v = CompilerTools.cast(v, needClass, c.getFile().getPoint(e.pos));
            int pos = e.pos;
            String data = c.getFile().getData();
            String ss = data.substring(pos);
            SourcePoint sp = c.getFile().getPoint(e.pos);
            return new Return(v, sp);
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
                    VBlock b = new VBlock(i, null, null);
                    b.add(oo);
                    i.thenBlock = b;
                }

            }

            if (e.elsepart != null) {
                Operation oo = c.st(e.elsepart, i);
                if (oo instanceof VBlock) {
                    i.elseBlock = (VBlock) oo;
                } else {
                    VBlock b = new VBlock(i, null, null);
                    if (oo != null)
                        b.add(oo);
                    i.elseBlock = b;
                }
            }
            return i;
        });

        addProcSt(JCTree.JCThrow.class, (c, e, o) -> {
            return new Throw((Value) c.op(e.expr, o), c.getFile().getPoint(e.pos));
        });

        addProcSt(JCTree.JCVariableDecl.class, (c, e, o) -> {
            SVar var = new SVar(e.name.toString(), TypeUtil.loadClass(c.getCurrentClass().getClassLoader(), e.type, c.getFile().getPoint(e.pos)), o);
            DeclareVar dv = new DeclareVar(var, c.getFile().getPoint(e.getType().pos));
            if (e.init == null)
                dv.init = OperationCompiler.getInitValueForType(var.getType(), c.getFile().getPoint(e.pos));
            else
                dv.init = CompilerTools.cast(c.op(e.init, o), var.getType(), c.getFile().getPoint(e.init.pos));
            return dv;
        });

        addProcSt(JCTree.JCEnhancedForLoop.class, (c, e, o) -> {
            Value v = c.op(e.expr, o);
            VClass classIterable = c.getCurrentClass().getClassLoader().loadClass(Iterable.class.getName(), c.getFile().getPoint(e.pos));

            if (v.getType().isParent(classIterable)) {

                VBlock block = new VBlock(o, null, null);


                VClass classIterator = c.getCurrentClass().getClassLoader().loadClass(Iterator.class.getName(), c.getFile().getPoint(e.pos));



                SVar iterator = new SVar("it" + Integer.toString(new Object().hashCode(), Character.MAX_RADIX), classIterator, block);
                DeclareVar it = new DeclareVar(iterator, c.getFile().getPoint(e.expr.pos));

                it.init = CodeBuilder.scope(v).method("iterator").invoke().build();//new Invoke(v.getType().getMethod("iterator", c.getFile().getStartPoint(e.pos)), v);
                block.add(it);
                WhileLoop wl = new WhileLoop(block, null);
                wl.value = CodeBuilder.scope(it.getVar()).method("hasNext").invoke().build();//new Invoke(classIterator.getMethod("hasNext", c.getFile().getStartPoint(e.pos)), );
                wl.block = new VBlock(wl, null, null);
                block.add(wl);

                SVar var = new SVar(e.var.name.toString(), TypeUtil.loadClass(c.getCurrentClass().getClassLoader(), e.var.type, c.getFile().getPoint(e.pos)), block);
                DeclareVar dv = new DeclareVar(var, c.getFile().getPoint(e.var.pos));
                dv.init = new Invoke(classIterator.getMethod("next", c.getFile().getPoint(e.pos)), it.getVar());
                wl.block.add(dv);
                wl.block.add(c.st(e.body, wl.block));
                return block;
            } else {
                VBlock block = new VBlock(o, null, null);

                VClass intClass = c.getCurrentClass().getClassLoader().loadClass("int", c.getFile().getPoint(e.expr.pos));
                SVar arVar = new SVar("l" + Integer.toString(new Object().hashCode(), Character.MAX_RADIX), v.getType(), block);
                DeclareVar ar = new DeclareVar(arVar, c.getFile().getPoint(e.expr.pos));
                ar.init = v;
                block.add(ar);

                ArrayClass ac = (ArrayClass)v.getType();
                ForLoop forLoop = new ForLoop(o, c.getFile().getPoint(e.pos));
                forLoop.block = new VBlock(forLoop, null, null);

                SVar itVar = new SVar("i" + Integer.toString(new Object().hashCode(), Character.MAX_RADIX), intClass, forLoop);
                DeclareVar it = new DeclareVar(itVar, c.getFile().getPoint(e.expr.pos));
                it.init = new Const(0, intClass);
                forLoop.init = it;
                forLoop.update = new Increment(itVar, Increment.IncType.PRE_INC, intClass);
                forLoop.value = new VBinar(itVar, new GetField(arVar, arVar.getType().getField("length", c.getFile().getPoint(e.expr.pos)), null), c.getCurrentClass().getClassLoader().loadClass("boolean", c.getFile().getPoint(e.pos)), VBinar.BitType.LT, null);

                SVar el = new SVar(e.var.name.toString(), TypeUtil.loadClass(c.getCurrentClass().getClassLoader(), e.var.type, c.getFile().getPoint(e.pos)), forLoop.block);
                DeclareVar dv = new DeclareVar(el, c.getFile().getPoint(e.var.pos));
                dv.init = new ArrayGet(arVar, itVar);
                forLoop.block.add(dv);


                if (e.body != null) {
                    forLoop.block.add(c.st(e.body, forLoop.block));
                }
                block.add(forLoop);
                return block;
            }
        });

        addProcSt(JCTree.JCSkip.class, (c,e,p)->null);

        addProcSt(JCTree.JCWhileLoop.class, (c, e, o) -> {
            Value v = c.op(e.cond, o);
            WhileLoop fe = new WhileLoop(o, c.getFile().getPoint(e.pos));
            fe.value = c.op(e.cond, o);
            Operation op = c.st(e.body, fe);
            if (!(op instanceof VBlock)) {
                VBlock b = new VBlock(fe, null, null);
                if (op != null)
                    b.add(op);
                op = b;
            }
            fe.block = (VBlock) op;
            return fe;
        });

        addProcSt(JCTree.JCDoWhileLoop.class, (c,e,o)->{
            Value v = c.op(e.cond, o);
            DoWhileLoop fe = new DoWhileLoop(o, c.getFile().getPoint(e.pos));
            fe.value = c.op(e.cond, o);
            Operation op = c.st(e.body, fe);
            if (!(op instanceof VBlock)) {
                VBlock b = new VBlock(fe, null, null);
                if (op != null)
                    b.add(op);
                op = b;
            }
            fe.block = (VBlock) op;
            return fe;
        });

        addProcSt(JCTree.JCForLoop.class, (c, e, o) -> {
            ForLoop f = new ForLoop(o, c.getFile().getPoint(e.pos));
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
                VBlock b = new VBlock(f, null, null);
                if (oo != null)
                    b.add(oo);
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
            return new Continue(l, c.getFile().getPoint(e.pos));
        });

        addProcSt(JCTree.JCBreak.class, (c, e, o) -> {
            Label l = null;
            if (e.label != null) {
                l = o.findLabel(e.label.toString()).get();
            }
            return new Break(l, c.getFile().getPoint(e.pos));
        });


        addProcSt(JCTree.JCTry.class, (c, e, o) -> {
            if (e.resources != null && !e.resources.isEmpty())
                throw new RuntimeException("Try with resurce not supported yet");
            Try tr = new Try(o, c.getFile().getPoint(e.pos));
            tr.block = (VBlock) c.st(e.body, o);
            for (JCTree.JCCatch ca : e.catchers) {
                SVar var = new SVar(ca.param.name.toString(), TypeUtil.loadClass(c.getCurrentClass().getClassLoader(), ca.param.type, c.getFile().getPoint(ca.pos)), null);
                DeclareVar dv = new DeclareVar(var, c.getFile().getPoint(ca.getParameter().pos));
                Try.Catch cc = new Try.Catch(tr, dv, c.getFile().getPoint(ca.pos));
                var.setParentContext(cc);
                if (ca.param.vartype instanceof JCTree.JCTypeUnion) {
                    JCTree.JCTypeUnion ut = (JCTree.JCTypeUnion)ca.param.vartype;
                    for (JCTree.JCExpression ee : ut.alternatives)
                        cc.classes.add(TypeUtil.loadClass(c.getCurrentClass().getClassLoader(), ee.type, c.getFile().getPoint(ee.pos)));
                } else
                    cc.classes.add(TypeUtil.loadClass(c.getCurrentClass().getClassLoader(), ca.param.vartype.type, c.getFile().getPoint(ca.pos)));
                cc.block = (VBlock) c.st(ca.body, cc);
                tr.catchs.add(cc);
            }
            return tr;
        });

        addProcSt(JCTree.JCSwitch.class, (c,e,o)->{
            Switch s = new Switch(o, c.op(e.selector, o), c.getFile().getPoint(e.pos));
            for (JCTree.JCCase cc : e.getCases()) {
                Switch.Case ca = new Switch.Case(s, c.getFile().getPoint(cc.pos));
                ca.value = cc.getExpression()==null?null:c.op(cc.getExpression(), s);
                ca.block = new VBlock(ca, null, null);
                for (JCTree.JCStatement ss : cc.getStatements()) {
                    Operation oo = c.st(ss, ca.block);
                    if (oo != null)
                        ca.block.add(oo);
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

    public static VMethod createBrig(VMethod from, VMethod to) {
        if (from.getParent() == to.getParent())
            throw new IllegalArgumentException("Can't create brige for self type");
        Objects.requireNonNull(from, "Argument \"from\" is NULL");
        Objects.requireNonNull(from, "Argument \"to\" is NULL");
        VMethod rep = new VMethod(null, from.getRealName(), to.getParent(), null);
        rep.setReplace(from);
        from.getArguments().forEach(e -> {
            rep.addArg(e);
        });
        rep.setBlock(new VBlock(rep, null, null));
        rep.alias = from.alias;

        Invoke inv = new Invoke(to, new This(to.getParent()));
        rep.getArguments().forEach(e -> {
            inv.arguments.add(e);
        });
        inv.returnType = to.returnType;


        Objects.requireNonNull(from.returnType, "return type of " + from + " is null");

        if (!(from.returnType instanceof ArrayClass) && from.returnType.isThis("void")) {
            rep.getBlock().add(inv);
        } else {
            rep.getBlock().add(new Return(inv, null));
        }
        return rep;
    }

    private static interface ProcSt<V extends JCTree.JCStatement> {
        Operation proc(TreeCompiler compiller, V e, Context context) throws CompileException;
    }

}
