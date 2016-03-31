package org.tlsys.twt.compiler;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import org.tlsys.InputsClassModificator;
import org.tlsys.OtherClassLink;
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
                classIns = ClassCompiler.createAnnonimusClass(o, e.def, c.getClassLoader());
                //throw new RuntimeException("Annonimus class not supported yet!");
            } else {
                classIns = c.loadClass(e.type);
            }

            VClass enumClass = classIns.getClassLoader().loadClass(Enum.class.getName());


            if (classIns.isParent(enumClass)) {
                VConstructor con = classIns.getConstructor(classIns.getClassLoader().loadClass(String.class.getName()), classIns.getClassLoader().loadClass("int"));
                NewClass nc = new NewClass(con, c.getFile().getPoint(e.pos));
                return nc;
            }

            List<VClass> argsParamClasses = buildConstructorInvokeTypes(classIns);//new ArrayList<VClass>();


            /*
            classIns.getModificator(ee->ee.getClass() == OtherClassLink.class).ifPresent(e3->{
                OtherClassLink o2 = (OtherClassLink)e3;
                argsParamClasses.add(o2.getToClass());
            });
            */

            for (JCTree.JCExpression ee : e.args)
                argsParamClasses.add(c.loadClass(ee.type));

            VConstructor con;
            try {
                con = classIns.getConstructor(argsParamClasses);
            } catch (CompileException ex) {
                throw ex;
            }

            NewClass nc = new NewClass(con, c.getFile().getPoint(e.pos));
            Optional<VClass> parentClass = con.getParent().getDependencyParent();

            for (VArgument arg : con.getArguments()) {
                if (arg.getCreator() != null) {
                    if (arg.getCreator() instanceof OtherClassLink.ArgumentLink) {
                        VClass aa = arg.getType();
                        nc.addArg(new This(arg.getType()));
                        continue;
                    }

                    if (arg.getCreator() instanceof InputsClassModificator.InputArgs) {
                        InputsClassModificator.InputArgs a = (InputsClassModificator.InputArgs) arg.getCreator();
                        nc.addArg(a.getInput());
                        continue;
                    }

                    throw new RuntimeException("Unknown creator " + arg.getCreator());
                }
            }

/*
            if (!(classIns instanceof AnnonimusClass) && parentClass.isPresent()) {
                Optional<VClass> currentParentClass = c.getCurrentClass().getDependencyParent();
                / *
                if (currentParentClass.isPresent() && currentParentClass.get()==parentClass.get()) {
                    nc.arguments.add(new GetField(new This(c.getCurrentClass()), TypeUtil.getParentThis(c.getCurrentClass())));
                } else
                * /
                nc.arguments.add(new This(parentClass.get()));
            }
            */


            for (JCTree.JCExpression ee : e.args)
                nc.arguments.add(c.op(ee, o));

            if (nc.arguments.size() != nc.constructor.getArguments().size())
                throw new RuntimeException("Difirent argument count");
            return nc;
        });

        addProc(JCTree.JCIdent.class, (c, e, o) -> {
            if (e.sym instanceof Symbol.ClassSymbol)
                return new StaticRef(c.loadClass(e.type));
            if (e.sym instanceof Symbol.VarSymbol && (e.toString().equals("this") || e.toString().equals("super")))
                return new This(c.loadClass(e.type));
            if (e.sym instanceof Symbol.VarSymbol) {
                Symbol.VarSymbol vv = (Symbol.VarSymbol) e.sym;
                Optional<Context> var = o.find(vv.name.toString(), v -> true);
                if (var.isPresent()) {
                    if (var.get() instanceof VField) {
                        VField field = (VField) var.get();
                        if (field.isStatic())
                            return new GetField(new StaticRef(field.getParent()), field);
                        else
                            return new GetField(new This(field.getParent()), field);
                    }
                    return (SVar) var.get();
                }
            }
            throw new RuntimeException("Unknown indent " + e + ", class=" + (e.sym != null ? e.sym.getClass().getName() : "NULL"));
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
                case NEG:
                    type = Increment.IncType.NEG;
                    break;
            }
            if (type == null)
                throw new RuntimeException("Unknown incremet type " + e.getTag());
            return new Increment(c.op(e.arg, o), type, c.loadClass(e.type));
        });

        addProc(JCTree.JCTypeCast.class, (c, e, o) -> {
            Value v = (Value) c.op(e.expr, o);
            VClass type = c.loadClass(e.type);
            return CompilerTools.cast(v, type);
            //return new Cast(type, v);
        });

        addProc(JCTree.JCLambda.class, (c, e, o) -> {
            VClass imp = c.loadClass(e.type);
            VMethod method = null;
            for (VMethod m : imp.methods)
                if (m.getBlock() == null) {
                    method = m;
                    break;
                }
            Objects.requireNonNull(method, "Method for replace not found");
            Lambda l = new Lambda(method, o);
            for (JCTree.JCVariableDecl v : e.params) {
                VArgument a = new VArgument(v.name.toString(), c.loadClass(v.type), false, false, l, null);
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
                    throw new RuntimeException("No blocked lambda not supportedf yet");
            }
            return l;
        });

        addProc(JCTree.JCMemberReference.class, (c, e, o) -> {
            if (e.sym instanceof Symbol.MethodSymbol) {
                VClass imp = c.loadClass(e.type);
                VMethod replaceMethod = null;
                for (VMethod m : imp.methods)
                    if (m.getBlock() == null) {
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

                if (in.name.toString().equals("this") || in.name.toString().equals("super")) {
                    VClass cc = null;
                    if (in.name.toString().equals("this"))
                        cc = c.getCurrentClass();
                    if (in.name.toString().equals("super")) {
                        cc = c.getCurrentClass().extendsClass;
                    }

                    if (cc == null)
                        return null;
                    //throw new RuntimeException("!!!");

                    List<VClass> args = buildConstructorInvokeTypes(cc);
                    for (JCTree.JCExpression ee : e.args) {
                        args.add(TypeUtil.loadClass(c.getClassLoader(), ee.type));
                    }

                    method = cc.getConstructor(args);

                } else {
                    List<VClass> args = new ArrayList<VClass>();
                    for (JCTree.JCExpression ee : e.args) {
                        args.add(TypeUtil.loadClass(c.getClassLoader(), ee.type));
                    }
                    try {
                        method = self.getType().getMethod(in.name.toString(), args);
                    } catch (CompileException ex) {
                        throw ex;
                    }
                }
                /*
                if (in.name.toString().equals("super") || in.name.toString().equals("this")) {
                    if (c.loadClass(m.owner.type).getRealName().contains("TArrayList"))
                        System.out.println("->");
                    method = c.loadClass(m.owner.type).getConstructor(m);
                } else {
                    //method = c.loadClass(m.owner.type).getMethod(m);

                }
                */
            }
            if (self == null || method == null)
                throw new RuntimeException("Self or method is NULL");

            if (method instanceof VConstructor && method.getParent().isParent(method.getParent().getClassLoader().loadClass(Enum.class.getName()))) {
                VBlock block = (VBlock) o;
                VConstructor cons = (VConstructor) block.getParentContext();
                return new Invoke(method, new This(cons.getParent())).addArg(cons.getArguments().get(0)).addArg(cons.getArguments().get(1));
            }
            if (method.isStatic())
                self = new StaticRef(method.getParent());

            Invoke i = new Invoke(method, self);


            int argInc = 0;
            if (method instanceof VConstructor) {
                for (VArgument arg : method.getArguments()) {
                    if (arg.getCreator() == null)
                        continue;

                    if (arg.getCreator().getClass() == OtherClassLink.ArgumentLink.class) {
                        OtherClassLink.ArgumentLink al = (OtherClassLink.ArgumentLink) arg.getCreator();

                        OtherClassLink ocl = (OtherClassLink) method.getParent().getModificator(ee -> ee.getClass() == OtherClassLink.class && ((OtherClassLink) ee).getToClass() == al.getToClass()).get();
                        i.addArg(new GetField(new This(ocl.getField().getParent()), ocl.getField()));
                        argInc++;
                        continue;
                    }

                    if (arg.getCreator().getClass() == InputsClassModificator.InputArgs.class) {
                        InputsClassModificator.InputArgs al = (InputsClassModificator.InputArgs) arg.getCreator();
                        //OtherClassLink ocl = (OtherClassLink) method.getParent().getModificator(ee -> ee.getClass() == OtherClassLink.class && ((OtherClassLink) ee).getToClass() == al.getToClass()).get();
                        i.addArg(al.getInput());
                        argInc++;
                        break;
                    }

                    throw new RuntimeException("Unknown exception");
                }
            }


            /*
            if (i.getMethod() instanceof VConstructor && self.getType().isParent(i.getMethod().getParent()) && i.getMethod().getParent().getDependencyParent().isPresent()) {
                argInc = 1;
                if (o instanceof VBlock && ((VBlock) o).getParentContext() instanceof VConstructor) {
                    VBlock block = (VBlock) o;
                    VConstructor cons = (VConstructor) block.getParentContext();
                    i.addArg(cons.getArguments().get(0));
                } /* else
                    i.addArg(new GetField(self, TypeUtil.getParentThis(self.getType())));
                    * /
            }
                */


            for (int c1 = argInc; c1 < method.getArguments().size(); c1++) {
                if (method.getArguments().get(c1).var) {
                    NewArrayItems nai = new NewArrayItems(method.getArguments().get(c1).getType().getArrayClass());
                    for (int c2 = c1; c2 < e.args.size(); c2++) {
                        nai.elements.add(c.op(e.args.get(c2 - argInc), o));
                    }
                    i.arguments.add(nai);
                    break;
                } else {
                    int index = c1 - argInc;
                    VArgument arg = i.getMethod().getArguments().get(index);
                    Value val = c.op(e.args.get(index), o);
                    val = CompilerTools.cast(val, arg.getType());
                    i.arguments.add(val);
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
                case MUL:
                    type = VBinar.BitType.MUL;
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
                case DIV:
                    type = VBinar.BitType.DIV;
                    break;
                case USR:
                    type = VBinar.BitType.USR;
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
                    return new ClassRef(scope.getType());
                }
            }

            if (e.sym instanceof Symbol.VarSymbol) {
                VClass scopeClass = scope.getType();
                VField field = (VField) scope.getType().find(((Symbol.VarSymbol) e.sym).name.toString(), v -> v instanceof VField).orElseThrow(() ->
                        new CompileException("Can't find field " + e.name.toString() + " in " + scopeClass.getRealName())
                );

                Optional<VClass> dep = c.getCurrentClass().getDependencyParent();

                /*
                if (field.getParent() != c.getCurrentClass() && dep.isPresent() && dep.get().isParent(field.getParent())) {
                    scope = new GetField(new This(c.getCurrentClass()), TypeUtil.getParentThis(c.getCurrentClass()));
                }
                */

                GetField gf = new GetField(scope, field);
                return gf;
            }

            if (e.sym instanceof Symbol.ClassSymbol) {
                String text = "scope = " + scope;
                return new StaticRef((VClass) scope.find(e.name.toString(), v -> v instanceof VClass).orElseThrow(() -> new VClassNotFoundException(e.sym.name.toString() + ", " + text)));
            }

            throw new RuntimeException("Unknown indent! " + e + ", " + e.sym.getClass());
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


        addProc(JCTree.JCPrimitiveTypeTree.class, (c, e, o) -> {
            return new StaticRef(TypeUtil.loadClass(c.getCurrentClass().getClassLoader(), e.type));
        });
    }

    private static List<VClass> buildConstructorInvokeTypes(VClass vClass) {
        Objects.requireNonNull(vClass);
        List<VClass> argsParamClasses = new ArrayList<VClass>();

        vClass.getModificator(ee -> ee.getClass() == OtherClassLink.class).ifPresent(e3 -> {
            OtherClassLink o2 = (OtherClassLink) e3;
            argsParamClasses.add(o2.getToClass());
        });

        vClass.getModificator(ee -> ee.getClass() == InputsClassModificator.class).ifPresent(e3 -> {
            InputsClassModificator o2 = (InputsClassModificator) e3;
            for (VField f : o2.getFields()) {
                argsParamClasses.add(f.getType());
            }
        });

        return argsParamClasses;
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

    public static <T extends Operation> T op(TreeCompiler compiler, JCTree.JCExpression tree, Context context) throws CompileException {
        ProcEx p = exProc.get(tree.getClass());
        if (p != null)
            return (T) p.proc(compiler, tree, context);
        throw new RuntimeException("Not supported " + tree.getClass().getName() + " \"" + tree + "\"");
    }



    /*
    private final VClass compileClass;

    public OperationCompiler(VClass compileClass) {
        this.compileClass = compileClass;
    }
    */

    private static interface ProcEx<V extends JCTree.JCExpression> {
        Operation proc(TreeCompiler compiller, V e, Context context) throws CompileException;
    }
}
