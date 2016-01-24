package org.tlsys.twt;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import org.tlsys.twt.annotations.*;
import org.tlsys.lex.Const;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;

import javax.lang.model.element.Modifier;
import java.util.*;

public class Compiller {
    private static final HashMap<Class, ProcEx> exProc = new HashMap<>();
    private static final HashMap<Class, ProcSt> stProc = new HashMap<>();

    static {
        addProc(JCTree.JCNewClass.class, (c, e, o) -> {
            ArrayList<VClass> aclass = new ArrayList<VClass>(e.args.size());
            Symbol.MethodSymbol ms = (Symbol.MethodSymbol) e.constructor;

            for (Symbol.VarSymbol ee : ms.getParameters())
                aclass.add(c.vClassLoader.loadClass(ee.type));
            VConstructor con = c.vClassLoader.loadClass(e.type).getConstructor((Symbol.MethodSymbol) e.constructor);
            NewClass nc = new NewClass(con);
            for (JCTree.JCExpression ee : e.args)
                nc.arguments.add(c.op(ee, o));
            return nc;
        });

        addProc(JCTree.JCIdent.class, (c, e, o) -> {
            if (e.sym instanceof Symbol.ClassSymbol)
                return new StaticRef(c.vClassLoader.loadClass(e.type));
            if (e.sym instanceof Symbol.VarSymbol && (e.toString().equals("this") || e.toString().equals("super")))
                return new This(c.vClassLoader.loadClass(e.type));
            if (e.sym instanceof Symbol.VarSymbol) {
                Optional<SVar> var = o.find((Symbol.VarSymbol) e.sym, v -> true);
                if (var.isPresent()) {
                    return var.get();
                }
            }
            throw new RuntimeException("Unknown indent " + e);
        });

        addProc(JCTree.JCLiteral.class, (c, e, o) -> {
            return new Const(e.getValue(), c.vClassLoader.loadClass(e.type));
        });

        addProc(JCTree.JCUnary.class, (c, e, o) -> {
            Increment.IncType type = null;
            switch (e.getTag()) {
                case NOT:
                    type = Increment.IncType.NOT;
                    break;
                case POSTDEC:
                    type = Increment.IncType.POST_DEC;
                    break;
                case PREDEC:
                    type = Increment.IncType.PRE_DEC;
                    break;
                case PREINC:
                    type = Increment.IncType.PRE_INC;
                    break;
                case POSTINC:
                    type = Increment.IncType.POST_INC;
                    break;
            }
            if (type == null)
                throw new RuntimeException("Unknown incremet type " + e.getTag());
            return new Increment((Value) c.op(e.arg, o), type, c.vClass.getClassLoader().loadClass(e.type));
        });

        addProc(JCTree.JCTypeCast.class, (c, e, o) -> {
            return new Cast(c.vClassLoader.loadClass(e.type), (Value) c.op(e.expr, o));
        });

        addProc(JCTree.JCLambda.class, (c, e, o) -> {
            VBlock block = new VBlock(o);
            VClass imp = c.vClassLoader.loadClass(e.type);
            VMethod method = null;
            for (VMethod m : imp.methods)
                if (m.block == null) {
                    method = m;
                    break;
                }
            Objects.requireNonNull(method, "Method for replace not found");
            Lambda l = new Lambda(block, method, o);
            for (JCTree.JCVariableDecl v : e.params) {
                VArgument a = new VArgument(c.vClassLoader.loadClass(v.type), v.sym);
                a.name = v.name.toString();
                l.arguments.add(a);
            }
            if (e.body instanceof JCTree.JCBlock) {
                c.st((JCTree.JCStatement) e.body, l);
            }
            return l;
        });

        addProc(JCTree.JCMemberReference.class, (c, e, o) -> {
            if (e.sym instanceof Symbol.MethodSymbol) {
                VClass imp = c.vClassLoader.loadClass(e.type);
                VMethod replaceMethod = null;
                for (VMethod m : imp.methods)
                    if (m.block == null) {
                        replaceMethod = m;
                        break;
                    }
                Value scope = c.op(e.expr, o);
                VMethod method = scope.getType().getMethod((Symbol.MethodSymbol) e.sym);
                return new FunctionRef(replaceMethod, method);
            }

            throw new RuntimeException("Unknown member referens");
        });

        addProc(JCTree.JCMethodInvocation.class, (c, e, o) -> {
            Value self = null;
            VExecute method = null;
            if (e.meth instanceof JCTree.JCFieldAccess) {
                JCTree.JCFieldAccess f = (JCTree.JCFieldAccess) e.meth;
                self = Objects.requireNonNull(c.op(f.selected, o));
                try {
                    method = self.getType().getMethod((Symbol.MethodSymbol) f.sym);
                } catch (NullPointerException ee) {
                    throw ee;
                }
            }
            if (self == null || method == null && e.meth instanceof JCTree.JCIdent) {
                JCTree.JCIdent in = (JCTree.JCIdent) e.meth;
                Symbol.MethodSymbol m = (Symbol.MethodSymbol) in.sym;
                if (in.name.toString().equals("super") || in.name.toString().equals("this")) {
                    self = new This(c.vClass);
                    method = c.vClass.getClassLoader().loadClass(m.owner.type).getConstructor(m);
                } else {
                    self = new This(c.vClass);
                    method = c.vClass.getClassLoader().loadClass(m.owner.type).getMethod(m);
                }
            }
            if (self == null || method == null)
                throw new RuntimeException("Self or method is NULL");

            //self.getType().getMethod(e)
            Invoke i = new Invoke(method, self);
            for (int c1 = 0; c1 < method.arguments.size(); c1++) {
                if (method.arguments.get(c1).var) {
                    NewArrayItems nai = new NewArrayItems(method.arguments.get(c1).getType().getArrayClass());
                    for (int c2 = c1; c2 < e.args.size(); c2++) {
                        nai.elements.add(c.op(e.args.get(c2), o));
                    }
                    i.arguments.add(nai);
                    break;
                } else {
                    i.arguments.add(c.op(e.args.get(c1), o));
                }
            }
            /*
            for (JCTree.JCExpression ee : e.args) {
                i.arguments.add(c.op(ee, o));
            }
            */
            return i;
        });

        addProc(JCTree.JCConditional.class, (c, e, o) -> {


            Conditional con = new Conditional(c.op(e.cond, o),
                    c.op(e.getTrueExpression(), o),
                    c.op(e.getFalseExpression(), o),
                    c.vClass.getClassLoader().loadClass(e.type));
            return con;
        });

        addProc(JCTree.JCBinary.class, (c, e, o) -> {
            VBinar.BitType type = null;
            switch (e.getTag()) {
                case EQ:
                    type = VBinar.BitType.EQ;
                    break;
                case OR:
                    type = VBinar.BitType.OR;
                    break;
                case AND:
                    type = VBinar.BitType.AND;
                    break;
                case NE:
                    type = VBinar.BitType.NE;
                    break;
                case PLUS:
                    type = VBinar.BitType.PLUS;
                    break;
                case MINUS:
                    type = VBinar.BitType.MINUS;
                    break;
                case LT:
                    type = VBinar.BitType.LT;
                    break;
                case GE:
                    type = VBinar.BitType.GE;
                    break;
                case GT:
                    type = VBinar.BitType.GT;
                    break;
                case LE:
                    type = VBinar.BitType.LE;
                    break;
                default:
                    throw new RuntimeException("Not supported binar operation " + e.getTag());
            }
            return new VBinar((Value) c.op(e.lhs, o), (Value) c.op(e.rhs, o), c.vClass.getClassLoader().loadClass(e.type), type);
        });

        addProc(JCTree.JCParens.class, (c, e, o) -> {
            return new Parens((Value) c.op(e.expr, o));
        });

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

        addProc(JCTree.JCAssign.class, (c, e, o) -> {
            Value v = c.op(e.lhs, o);
            Value v2 = c.op(e.rhs, o);
            if (v instanceof GetField) {
                GetField gf = (GetField) v;
                SetField sf = new SetField(gf.getScope(), gf.getField(), v2, Assign.AsType.ASSIGN);
                return sf;
            }
            if (v instanceof ArrayGet) {
                ArrayGet gf = (ArrayGet) v;
                ArrayAssign aa = new ArrayAssign(gf.getValue(), gf.getIndex(), v2, Assign.AsType.ASSIGN);
                return aa;
            }
            Assign a = new Assign(c.op(e.lhs, o), v2, c.vClass.getClassLoader().loadClass(e.type), Assign.AsType.ASSIGN);
            return a;
        });

        addProc(JCTree.JCAssignOp.class, (c, e, o) -> {
            Value v = c.op(e.lhs, o);
            Value v2 = c.op(e.rhs, o);
            Assign.AsType type = null;
            switch (e.getTag()) {
                case PLUS_ASG:
                    type = Assign.AsType.PLUS;
                    break;
                case MINUS_ASG:
                    type = Assign.AsType.MINUS;
                    break;
                default:
                    throw new RuntimeException("Unknown type " + e.getTag());
            }
            if (v instanceof GetField) {
                GetField gf = (GetField) v;
                SetField sf = new SetField(gf.getScope(), gf.getField(), v2, type);
                return sf;
            }
            if (v instanceof ArrayGet) {
                ArrayGet gf = (ArrayGet) v;
                ArrayAssign aa = new ArrayAssign(gf.getValue(), gf.getIndex(), v2, type);
                return aa;
            }
            Assign a = new Assign(c.op(e.lhs, o), v2, c.vClass.getClassLoader().loadClass(e.type), type);
            return a;
        });

        addProcSt(JCTree.JCVariableDecl.class, (c, e, o) -> {
            SVar var = new SVar(c.vClass.getClassLoader().loadClass(e.type), e.sym);
            DeclareVar dv = new DeclareVar(var);
            if (e.init == null)
                dv.init = c.init(var.getType());
            else
                dv.init = c.op(e.init, o);
            var.name = e.name.toString();
            return dv;
        });

        addProc(JCTree.JCInstanceOf.class, (c, e, o) -> {
            InstanceOf i = new InstanceOf(c.op(e.expr, o), c.vClass.getClassLoader().loadClass(e.clazz.type));
            return i;
        });

        addProcSt(JCTree.JCEnhancedForLoop.class, (c, e, o) -> {
            SVar var = new SVar(c.vClass.getClassLoader().loadClass(e.var.type), e.var.sym);
            DeclareVar dv = new DeclareVar(var);
            var.name = e.var.name.toString();
            Value v = c.op(e.expr, o);
            ForEach fe = new ForEach(v, dv, o);
            Operation op = c.st(e.body, fe);
            if (!(op instanceof VBlock)) {
                VBlock b = new VBlock(fe);
                b.operations.add(op);
                op = b;
            }
            fe.block = (VBlock) op;
            return fe;
        });

        addProcSt(JCTree.JCWhileLoop.class, (c, e, o) -> {
            Value v = c.op(e.cond, o);
            WhileLoop fe = new WhileLoop(o);
            Operation op = c.st(e.body, fe);
            if (!(op instanceof VBlock)) {
                VBlock b = new VBlock(fe);
                b.operations.add(op);
                op = b;
            }
            fe.block = (VBlock) op;
            return fe;
        });

        addProc(JCTree.JCFieldAccess.class, (c, e, o) -> {
            Value scope = c.op(e.selected, o);
            if (scope instanceof StaticRef) {
                String name = e.name.toString();
                if (name.equals("this")) {
                    return c.vClass.getParentVar();
                }
            }
            VField field = (VField) scope.getType().find((Symbol.VarSymbol) e.sym, v -> true).orElseThrow(() ->
                            new RuntimeException("Can't find field " + e.name.toString() + " in " + scope.getType().fullName)
            );
            GetField gf = new GetField(scope, field);
            return gf;
        });

        addProc(JCTree.JCNewArray.class, (c, e, o) -> {
            if (e.dims != null) {
                NewArrayLen nal = new NewArrayLen((ArrayClass) c.vClass.getClassLoader().loadClass(e.type));
                for (JCTree.JCExpression v : e.dims)
                    nal.sizes.add(c.op(v, o));
                return nal;
            }

            if (e.elems != null) {
                NewArrayItems nai = new NewArrayItems((ArrayClass) c.vClass.getClassLoader().loadClass(e.type));
                for (JCTree.JCExpression v : e.dims)
                    nai.elements.add(c.op(v, o));
                return nai;
            }

            throw new RuntimeException("Unknown array new operation");
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

        addProc(JCTree.JCArrayAccess.class, (c, e, o) -> {
            /*
            ArrayAssign aa = new ArrayAssign(c.op(e.getExpression(), o));
            aa.indexs.add(c.op(e.index, o));
            return aa;
            */

            ArrayGet ag = new ArrayGet(c.op(e.getExpression(), o), c.op(e.getIndex(), o));
            return ag;
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
                SVar var = new SVar(c.vClass.getClassLoader().loadClass(ca.param.type), ca.param.sym);
                DeclareVar dv = new DeclareVar(var);
                var.name = ca.param.name.toString();
                Try.Catch cc = new Try.Catch(tr, dv);
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

    private final VClassLoader vClassLoader;
    private final VClass vClass;

    public Compiller(VClassLoader vClassLoader, VClass vClass) {
        this.vClassLoader = vClassLoader;
        this.vClass = vClass;
    }

    private static int modToFlag(Set<Modifier> mod) {
        int out = 0;
        if (mod.contains(Modifier.PUBLIC))
            out = out | java.lang.reflect.Modifier.PUBLIC;
        if (mod.contains(Modifier.PROTECTED))
            out = out | java.lang.reflect.Modifier.PROTECTED;
        if (mod.contains(Modifier.PRIVATE))
            out = out | java.lang.reflect.Modifier.PRIVATE;
        if (mod.contains(Modifier.STATIC))
            out = out | java.lang.reflect.Modifier.STATIC;
        if (mod.contains(Modifier.FINAL))
            out = out | java.lang.reflect.Modifier.FINAL;
        if (mod.contains(Modifier.TRANSIENT))
            out = out | java.lang.reflect.Modifier.TRANSIENT;
        if (mod.contains(Modifier.VOLATILE))
            out = out | java.lang.reflect.Modifier.VOLATILE;
        if (mod.contains(Modifier.SYNCHRONIZED))
            out = out | java.lang.reflect.Modifier.SYNCHRONIZED;
        if (mod.contains(Modifier.NATIVE))
            out = out | java.lang.reflect.Modifier.NATIVE;
        if (mod.contains(Modifier.STRICTFP))
            out = out | java.lang.reflect.Modifier.STRICT;
        return out;
    }

    private static <V extends JCTree.JCExpression> void addProc(Class<V> cl, ProcEx<V> proc) {
        exProc.put(cl, proc);
    }

    private static <V extends JCTree.JCStatement> void addProcSt(Class<V> cl, ProcSt<V> proc) {
        stProc.put(cl, proc);
    }

    public <T extends Operation> T op(JCTree.JCExpression tree, Context context) throws VClassNotFoundException, MethodNotFoundException {
        ProcEx p = exProc.get(tree.getClass());
        if (p != null)
            return (T) p.proc(this, tree, context);
        throw new RuntimeException("Not supported " + tree.getClass().getName() + " \"" + tree + "\"");
    }

    public Operation st(JCTree.JCStatement sta, Context context) throws MethodNotFoundException, VClassNotFoundException {
        ProcSt p = stProc.get(sta.getClass());
        if (p != null)
            return p.proc(this, sta, context);
        throw new RuntimeException("Not supported " + sta.getClass().getName() + " \"" + sta + "\"");
    }

    public Operation init(VClass vClass) throws VClassNotFoundException {
        if (vClass.fullName.equals("byte") || vClass.fullName.equals("short") || vClass.fullName.equals("int") || vClass.fullName.equals("long"))
            return new Const(0, vClass);
        if (vClass.fullName.equals("float") || vClass.fullName.equals("double"))
            return new Const(0.0f, vClass);
        if (vClass.fullName.equals("boolean"))
            return new Const(false, vClass);
        if (vClass.fullName.equals("char"))
            return new Cast(vClassLoader.loadClass(int.class.getName()), new Const((char) 0, vClass));
        return new Const("null", vClassLoader.loadClass(Object.class.getName()));
    }

    public Member memDec(JCTree decl) throws VClassNotFoundException {
        if (decl instanceof JCTree.JCMethodDecl) {
            JCTree.JCMethodDecl m = (JCTree.JCMethodDecl) decl;
            if (m.name.toString().equals("<init>")) {
                VConstructor v = consDec(m);
                vClass.constructors.add(v);
                return v;
            }
            VMethod v = memDec(m);
            vClass.methods.add(v);
            return v;
        }

        if (decl instanceof JCTree.JCVariableDecl) {
            return fieldDec((JCTree.JCVariableDecl) decl);
        }
        if (decl instanceof JCTree.JCClassDecl) {
            return null;
        }

        throw new RuntimeException("Not supported " + decl.getClass().getName() + " " + decl);
    }

    public void exeCode(VExecute method, JCTree.JCMethodDecl dec) throws MethodNotFoundException, VClassNotFoundException {
        if (dec.body == null)
            return;

        method.block = (VBlock) st(dec.body, method);
    }

    public VField fieldDec(JCTree.JCVariableDecl fie) throws VClassNotFoundException {

        VField v = new VField(vClassLoader.loadClass(fie.type), modToFlag(fie.getModifiers().getFlags()), fie.sym, vClass);
        v.name = fie.getName().toString();
        vClass.fields.add(v);
        return v;
    }


    public static String getInvokeGenerator(JCTree.JCModifiers modifiers) {
        for (JCTree.JCAnnotation an : modifiers.getAnnotations()) {
            if (an.type.toString().equals(org.tlsys.twt.annotations.InvokeGen.class.getName())) {
                JCTree.JCAssign a = (JCTree.JCAssign) an.getArguments().get(0);
                JCTree.JCFieldAccess val = (JCTree.JCFieldAccess) a.getExpression();
                String codeGenerator = ""+val.type.toString().substring(Class.class.getName().length()+1);//val.selected.toString();
                return codeGenerator.substring(0, codeGenerator.length()-1);
            }
        }
        return null;
    }

    public void exeDec(JCTree.JCMethodDecl mem, VExecute m) throws VClassNotFoundException {
        m.generator = GenPlugin.getGenerator(mem.getModifiers());
        m.invokeGenerator = getInvokeGenerator(mem.getModifiers());
        m.setModificators(modToFlag(mem.getModifiers().getFlags()));
        for (JCTree.JCVariableDecl v : mem.getParameters()) {
            //Set<Modifier> mm = v.getModifiers().getFlags();

            VClass arg = vClassLoader.loadClass(v.type);
            if (arg instanceof ArrayClass) {
                //VClass arg2 = vClassLoader.loadClass(v.type);
            }
            VArgument a = new VArgument(arg, v.sym);
            a.var = (v.mods.flags & Flags.VARARGS) != 0;
            a.name = v.name.toString();
            m.arguments.add(a);
        }
    }

    private VMethod memDec(JCTree.JCMethodDecl mem) throws VClassNotFoundException {
        VMethod m = new VMethod(vClass, null, mem.sym);
        if (!mem.getModifiers().annotations.isEmpty()) {
            for (JCTree.JCAnnotation a : mem.getModifiers().annotations) {
                if (a.type.toString().equals(MethodName.class.getName())) {
                    JCTree.JCAssign aa = (JCTree.JCAssign) a.args.get(0);
                    m.alias = (String) ((JCTree.JCLiteral) aa.rhs).getValue();
                }
            }
        }
        m.name = mem.getName().toString();
        m.returnType = vClass.getClassLoader().loadClass(mem.restype.type);
        exeDec(mem, m);
        return m;
    }

    private VConstructor consDec(JCTree.JCMethodDecl mem) throws VClassNotFoundException {
        VConstructor con = new VConstructor(vClass, mem.sym);
        con.setModificators(modToFlag(mem.getModifiers().getFlags()));
        exeDec(mem, con);
        return con;
    }

    private static interface ProcEx<V extends JCTree.JCExpression> {
        Operation proc(Compiller compiller, V e, Context context) throws VClassNotFoundException, MethodNotFoundException;
    }

    private static interface ProcSt<V extends JCTree.JCStatement> {
        Operation proc(Compiller compiller, V e, Context context) throws VClassNotFoundException, MethodNotFoundException;
    }
}
