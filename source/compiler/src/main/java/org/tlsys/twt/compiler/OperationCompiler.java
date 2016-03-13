package org.tlsys.twt.compiler;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import org.tlsys.TypeUtil;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.CompileException;

import java.util.*;

class OperationCompiler {

    private static final Map<Class, ProcEx> exProc = new HashMap<>();

    static {
        addProc(JCTree.JCNewClass.class, (c, e, o) -> {
            ArrayList<VClass> aclass = new ArrayList<VClass>(e.args.size());
            Symbol.MethodSymbol ms = (Symbol.MethodSymbol) e.constructor;

            for (Symbol.VarSymbol ee : ms.getParameters())
                aclass.add(c.loadClass(ee.type));
            VClass classIns = null;
            if (e.def != null) {
                classIns = ClassCompiler.createAnnonimusClass(e.def, c.getClassLoader());
                //throw new RuntimeException("Annonimus class not supported yet!");
            } else {
                classIns = c.loadClass(e.type);
            }
            
            VClass enumClass = classIns.getClassLoader().loadClass(Enum.class.getName());


            if (classIns.isParent(enumClass)) {
                VConstructor con = classIns.getConstructor(classIns.getClassLoader().loadClass(String.class.getName()), classIns.getClassLoader().loadClass("int"));
                NewClass nc = new NewClass(con);
                return nc;
            }
            
            VConstructor con = classIns.getConstructor((Symbol.MethodSymbol) e.constructor);
            
            NewClass nc = new NewClass(con);
            Optional<VClass> parentClass = con.getParent().getDependencyParent();
            
            if (parentClass.isPresent()) {
                Optional<VClass> currentParentClass = c.getCurrentClass().getDependencyParent();
                if (currentParentClass.isPresent() && currentParentClass.get()==parentClass.get()) {
                    nc.arguments.add(new GetField(new This(c.getCurrentClass()), TypeUtil.getParentThis(c.getCurrentClass())));
                } else
                    nc.arguments.add(new This(parentClass.get()));
            }
            
            for (JCTree.JCExpression ee : e.args)
                nc.arguments.add(c.op(ee, o));
            return nc;
        });

        addProc(JCTree.JCIdent.class, (c, e, o) -> {
            if (e.sym instanceof Symbol.ClassSymbol)
                return new StaticRef(c.loadClass(e.type));
            if (e.sym instanceof Symbol.VarSymbol && (e.toString().equals("this") || e.toString().equals("super")))
                return new This(c.loadClass(e.type));
            if (e.sym instanceof Symbol.VarSymbol) {
                Symbol.VarSymbol vv = (Symbol.VarSymbol)e.sym;
                Optional<SVar> var = o.find(vv.name.toString(), v -> true);
                if (var.isPresent()) {
                    if (var.get() instanceof VField) {
                        VField field = (VField)var.get();
                        if (field.isStatic())
                            return new GetField(new StaticRef(field.getParent()),field);
                        else
                            return new GetField(new This(field.getParent()), field);
                    }
                    return var.get();
                }
            }
            throw new RuntimeException("Unknown indent " + e + ", class=" + (e.sym!=null?e.sym.getClass().getName():"NULL"));
        });

        addProc(JCTree.JCLiteral.class, (c, e, o) -> {
            return new Const(e.getValue(), c.loadClass(e.type));
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
            return new Increment(c.op(e.arg, o), type, c.loadClass(e.type));
        });

        addProc(JCTree.JCTypeCast.class, (c, e, o) -> {
            return new Cast(c.loadClass(e.type), (Value) c.op(e.expr, o));
        });

        addProc(JCTree.JCLambda.class, (c, e, o) -> {
            VClass imp = c.loadClass(e.type);
            VMethod method = null;
            for (VMethod m : imp.methods)
                if (m.block == null) {
                    method = m;
                    break;
                }
            Objects.requireNonNull(method, "Method for replace not found");
            Lambda l = new Lambda(method, o);
            for (JCTree.JCVariableDecl v : e.params) {
                VArgument a = new VArgument(v.name.toString(), c.loadClass(v.type), false, false);
                l.arguments.add(a);
            }
            if (e.body instanceof JCTree.JCBlock) {
                l.setBlock((VBlock) c.st((JCTree.JCStatement) e.body, l));
            } else {
                if (e.body instanceof JCTree.JCExpression) {
                    VBlock block = new VBlock(l);
                    Operation op = c.op((JCTree.JCExpression) e.body, block);
                    if (l.getMethod().returnType != c.loadClass("void")) {
                        block.add(new Return((Value) op));
                    } else
                        block.add(op);
                    l.setBlock(block);
                } else
                    throw new RuntimeException("No blocked lambdanot supportedf yet");
            }
            return l;
        });

        addProc(JCTree.JCMemberReference.class, (c, e, o) -> {
            if (e.sym instanceof Symbol.MethodSymbol) {
                VClass imp = c.loadClass(e.type);
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
                    VClass selectedCalss = c.loadClass(f.selected.type);
                    method = selectedCalss.getMethod((Symbol.MethodSymbol) f.sym);
                } catch (MethodNotFoundException ee) {
                    throw ee;
                }
            }
            if (self == null || method == null && e.meth instanceof JCTree.JCIdent) {
                JCTree.JCIdent in = (JCTree.JCIdent) e.meth;
                Symbol.MethodSymbol m = (Symbol.MethodSymbol) in.sym;
                self = new This(c.getCurrentClass());
                if (in.name.toString().equals("super") || in.name.toString().equals("this")) {
                    method = c.loadClass(m.owner.type).getConstructor(m);
                } else {
                    method = c.loadClass(m.owner.type).getMethod(m);
                }
            }
            if (self == null || method == null)
                throw new RuntimeException("Self or method is NULL");

            if (method instanceof VConstructor && method.getParent().isParent(method.getParent().getClassLoader().loadClass(Enum.class.getName()))) {
                VBlock block = (VBlock)o;
                VConstructor cons = (VConstructor)block.getParentContext();
                return new Invoke(method, new This(cons.getParent())).addArg(cons.arguments.get(0)).addArg(cons.arguments.get(1));
            }
            if (method.isStatic())
                self = new StaticRef(method.getParent());
            else {
                if (! (method instanceof VConstructor)) {
                    Optional<VClass> dep = c.getCurrentClass().getDependencyParent();

                    if (method.getParent() != c.getCurrentClass() && dep.isPresent() && dep.get().isParent(method.getParent())) {
                        self = new GetField(new This(c.getCurrentClass()), TypeUtil.getParentThis(c.getCurrentClass()));
                    }
                }
            }

            Invoke i = new Invoke(method, self);

            int argInc = 0;
            if (i.getMethod() instanceof VConstructor && self.getType().isParent(i.getMethod().getParent()) && i.getMethod().getParent().getDependencyParent().isPresent()) {
                argInc=1;
                if (o instanceof VBlock && ((VBlock)o).getParentContext() instanceof VConstructor) {
                    VBlock block = (VBlock)o;
                    VConstructor cons = (VConstructor)block.getParentContext();
                    i.addArg(cons.arguments.get(0));
                } else
                    i.addArg(new GetField(self, TypeUtil.getParentThis(self.getType())));
            }

            for (int c1 = argInc; c1 < method.arguments.size(); c1++) {
                if (method.arguments.get(c1).var) {
                    NewArrayItems nai = new NewArrayItems(method.arguments.get(c1).getType().getArrayClass());
                    for (int c2 = c1; c2 < e.args.size(); c2++) {
                        nai.elements.add(c.op(e.args.get(c2 - argInc), o));
                    }
                    i.arguments.add(nai);
                    break;
                } else {
                    i.arguments.add(c.op(e.args.get(c1 - argInc), o));
                }
            }
            i.returnType = c.loadClass(e.type);
            return i;
        });

        addProc(JCTree.JCConditional.class, (c, e, o) -> {


            Conditional con = new Conditional(c.op(e.cond, o),
                    c.op(e.getTrueExpression(), o),
                    c.op(e.getFalseExpression(), o),
                    c.loadClass(e.type));
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
                case LT://>
                    type = VBinar.BitType.LT;
                    break;
                case GE://>=
                    type = VBinar.BitType.GE;
                    break;
                case GT://<
                    type = VBinar.BitType.GT;
                    break;
                case LE://<=
                    type = VBinar.BitType.LE;
                    break;
                case BITAND:
                    type = VBinar.BitType.BITAND;
                    break;
                case BITOR:
                    type = VBinar.BitType.BITOR;
                    break;
                case BITXOR:
                    type = VBinar.BitType.BITXOR;
                    break;
                case MOD:
                    type = VBinar.BitType.MOD;
                    break;
                default:
                    throw new RuntimeException("Not supported binar operation " + e.getTag());
            }
            return new VBinar((Value) c.op(e.lhs, o), (Value) c.op(e.rhs, o), c.loadClass(e.type), type);
        });

        addProc(JCTree.JCParens.class, (c, e, o) -> {
            return new Parens((Value) c.op(e.expr, o));
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
                ArrayAssign aa = new ArrayAssign(gf.getValue(), v2, gf.getIndex(), Assign.AsType.ASSIGN);
                return aa;
            }
            Assign a = new Assign(c.op(e.lhs, o), v2, c.loadClass(e.type), Assign.AsType.ASSIGN);
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
            Assign a = new Assign(c.op(e.lhs, o), v2, c.loadClass(e.type), type);
            return a;
        });

        addProc(JCTree.JCInstanceOf.class, (c, e, o) -> {
            InstanceOf i = new InstanceOf(c.op(e.expr, o), c.loadClass(e.clazz.type));
            return i;
        });

        addProc(JCTree.JCFieldAccess.class, (c, e, o) -> {
            Value scope = c.op(e.selected, o);
            if (scope instanceof StaticRef) {
                String name = e.name.toString();
                
                if (name.equals("this")) {
                    return new This(c.getCurrentClass().getParent());
                    //return new GetField(new This(c.getCurrentClass()), TypeUtil.getParentThis(c.getCurrentClass()));
                    //throw new RuntimeException("Not supported parent this parent class");
                    //return c.getCurrentClass().getParentVar();
                }
                

                if (name.equals("class")) {
                    return scope;
                }
            }
            VField field = (VField) scope.getType().find(((Symbol.VarSymbol) e.sym).name.toString(), v -> true).orElseThrow(() ->
                    new CompileException("Can't find field " + e.name.toString())
            );

            Optional<VClass> dep = c.getCurrentClass().getDependencyParent();

            if (field.getParent() != c.getCurrentClass() && dep.isPresent() && dep.get().isParent(field.getParent())) {
                scope = new GetField(new This(c.getCurrentClass()), TypeUtil.getParentThis(c.getCurrentClass()));
            }

            GetField gf = new GetField(scope, field);
            return gf;
        });

        addProc(JCTree.JCNewArray.class, (c, e, o) -> {
            if (e.dims != null && !e.dims.isEmpty()) {
                NewArrayLen nal = new NewArrayLen((ArrayClass) c.loadClass(e.type));
                for (JCTree.JCExpression v : e.dims)
                    nal.sizes.add(c.op(v, o));
                return nal;
            }

            if (e.elems != null) {
                NewArrayItems nai = new NewArrayItems((ArrayClass) c.loadClass(e.type));
                for (JCTree.JCExpression v : e.elems)
                    nai.elements.add(c.op(v, o));
                return nai;
            }

            throw new RuntimeException("Unknown array new operation");
        });

        addProc(JCTree.JCArrayAccess.class, (c, e, o) -> {
            ArrayGet ag = new ArrayGet(c.op(e.getExpression(), o), c.op(e.getIndex(), o));
            return ag;
        });


        addProc(JCTree.JCPrimitiveTypeTree.class, (c,e,o)->{
            return new StaticRef(TypeUtil.loadClass(c.getCurrentClass().getClassLoader(), e.type));
        });
    }

    public static Value getInitValueForType(VClass clazz) throws VClassNotFoundException {
        Objects.requireNonNull(clazz, "Argument clazz is NULL");
        if (clazz.isThis("byte") || clazz.isThis("short") || clazz.isThis("int") || clazz.isThis("long"))
            return new Const(0, clazz);
        if (clazz.isThis("float") || clazz.isThis("double"))
            return new Const(0.0f, clazz);
        if (clazz.isThis("boolean"))
            return new Const(false, clazz);
        if (clazz.isThis("char"))
            return new Cast(clazz.getClassLoader().loadClass("int"), new Const((char) 0, clazz));
        return new Const(null, clazz);
    }

    private static <V extends JCTree.JCExpression> void addProc(Class<V> cl, ProcEx<V> proc) {
        Objects.requireNonNull(cl, "Argument #0 is null");
        Objects.requireNonNull(proc, "Argument #1 is null");
        exProc.put(cl, proc);
    }

    private static interface ProcEx<V extends JCTree.JCExpression> {
        Operation proc(TreeCompiler compiller, V e, Context context) throws CompileException;
    }



    /*
    private final VClass compileClass;

    public OperationCompiler(VClass compileClass) {
        this.compileClass = compileClass;
    }
    */

    public static <T extends Operation> T op(TreeCompiler compiler, JCTree.JCExpression tree, Context context) throws CompileException {
        ProcEx p = exProc.get(tree.getClass());
        if (p != null)
            return (T) p.proc(compiler, tree, context);
        throw new RuntimeException("Not supported " + tree.getClass().getName() + " \"" + tree + "\"");
    }
}
