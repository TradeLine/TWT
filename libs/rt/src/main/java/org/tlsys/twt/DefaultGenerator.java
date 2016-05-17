package org.tlsys.twt;

import org.tlsys.CodeBuilder;
import org.tlsys.Outbuffer;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.classes.ArrayBuilder;
import org.tlsys.twt.classes.ClassRecord;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;

public class DefaultGenerator implements ICodeGenerator {


    private static final HashSet<VClass> generatedClasses = new HashSet<>();
    private static HashMap<Class, Gen> generators = new HashMap<>();

    static {
        addGen(Const.class, (c, o, p, g) -> {
            if (o.getValue() == null) {
                p.append("null");
                return true;
            }
            if (o.getValue() instanceof String || o.getValue() instanceof Character) {
                p.append("'").append(o.getValue().toString().replace("'", "\\'")).append("'");
                return true;
            }
            p.append(o.getValue().toString());
            return true;
        });

        addGen(Return.class, (c, o, p, g) -> {
            p.add(o.getStartPoint());
            p.append("return");
            if (o.getValue() != null) {
                p.append(" ");
                g.operation(c, o.getValue(), p);
            }
            return true;
        });

        addGen(This.class, (c, o, p, g) -> {
            if (!c.getCurrentClass().isParent(o.getType())) {
                //Optional<VClass> cl = c.getCurrentClass().getDependencyParent();
                /*
                if (cl.isPresent() && cl.get() == o.getType()) {
                    System.out.println("getting this of parent " + c.getCurrentClass().hashCode() + " " + c.getCurrentClass());
                    return g.operation(c, TypeUtil.getParentThis(c.getCurrentClass()), p);
                }
                */
                throw new RuntimeException(new CompileException("Not support other this type. Current " + c.getCurrentClass() + ", this=" + o.getType(), o.getStartPoint()));
            }
            p.append("this");
            return true;
        });

        addGen(Invoke.class, (c, o, p, g) -> {
            InvokeGenerator icg = c.getInvokeGenerator(o.getMethod());
            if (icg != null) {
                return icg.generate(c, o, p);
            }
            ICodeGenerator icg2 = c.getGenerator(o.getMethod());
            if (icg2 != null && icg2 != g)
                return icg2.operation(c, o, p);

            Predicate<Boolean> printArg = f -> {
                try {
                    boolean first = f;
                    for (Value v : o.arguments) {
                        if (!first)
                            p.append(",");
                        g.operation(c, v, p);
                        first = false;
                    }
                    return true;
                } catch (CompileException e) {
                    throw new RuntimeException(e);
                }
            };

            if (o.getScope() instanceof This) {//вызов конструктора

                This self = (This) o.getScope();

                if (o.getMethod().getParent() != o.getScope().getType() || o.getMethod() instanceof VConstructor) {//чужого
                    p.append("/*CALL OTHER CONSTRUCTOR*/");
                    int pos = p.getCurrent();
                    c.getGenerator(self.getType()).operation(c, new StaticRef(o.getMethod().getParent(), o.getStartPoint()), p);
                    p.append(".");

                    if (!o.getMethod().isStatic()) {
                        p.append("prototype.");
                        //p.append(o.getScope().getType().getClassLoader().loadClass(Class.class.getName()).getMethod("getJsClass").getRunTimeName()).append("()").append(".prototype.");
                    }
                    p.append(o.getMethod().getRunTimeName());
                    p.append(".call(this");
                    printArg.test(false);
                    p.append(")");
                    return true;
                }
            }
            int pos = p.getCurrent();

            //p.add("", o.getStartPoint());
            //p.pushHold(o.getStartPoint());

            g.operation(c, o.getScope(), p);

            //p.add("", o.getStartPoint());//Before point

            p.append(".");
            if (o.getMethod() instanceof VMethod) {
                VMethod m = (VMethod) o.getMethod();
                p.append(o.getMethod().getRunTimeName());
            } else
                p.append(o.getMethod().getRunTimeName());

            //p.add("", o.getEndPoint());//Before "("

            //p.popHold();


            p.append("(");
            printArg.test(true);
            p.append(")");
            //p.popHold();
            return true;
        });

        addGen(VArgument.class, (c, o, p, g) -> {
            p.append(o.getRuntimeName());
            return true;
        });

        addGen(DeclareVar.class, (c, o, p, g) -> {
            p.add(o.getPoint(), o.getVar().getRealName());
            p.append("var ").append(o.getVar().getRuntimeName());
            if (o.init != null) {
                p.append("=");
                g.operation(c, o.init, p);
            }
            return true;
        });
        addGen(GetField.class, (c, o, p, g) -> {
            int pos = p.getCurrent();
            if (Modifier.isFinal(o.getField().getModificators()) && o.getField().init != null && (o.getField().init instanceof Const)) {
                return g.operation(c, o.getField().init, p);
            }
            g.operation(c, o.getScope(), p);
            p.append(".");
            p.append(o.getField().getRuntimeName());
            //p.append();
            return true;
        });

        addGen(ClassRecordRef.class, (c, o, p, g) -> {
            ICodeGenerator icg = c.getGenerator(o.getType());
            if (icg != null && icg != g)
                return icg.operation(c, o, p);

            if (o.getToClass() instanceof ArrayClass) {
                ArrayClass ac = (ArrayClass) o.getToClass();
                return g.operation(c,
                        CodeBuilder.scope(new ClassRecordRef(ac.getComponent(), o.getStartPoint()))
                                .method("getArrayClassRecord")
                                .invoke(o.getStartPoint(), null)
                                .build(),
                        p);

            }

            p.append(Generator.storage.getRuntimeName());
            p.append(".");
            p.append(o.getToClass().fullName);
            return true;
        });

        addGen(StaticRef.class, (c, o, p, g) -> {
            ICodeGenerator icg = c.getGenerator(o.getType());
            if (icg != null && icg != g)
                return icg.operation(c, o, p);



            g.operation(c, new ClassRecordRef(o.getType(), o.getStartPoint()), p);
            p.append(".");
            p.append(CodeBuilder.scope(new ClassRecordRef(o.getType().getClassLoader().loadClass(ClassRecord.class.getName(), o.getStartPoint()), o.getStartPoint())).method("getPrototype").find().getRunTimeName());
            p.append("()");


            return true;
        });
        addGen(ClassRef.class, (c, o, p, g) -> {

            g.operation(c, new ClassRecordRef(o.refTo, o.getStartPoint()), p);
            p.append(".");
            p.append(CodeBuilder.scope(new ClassRecordRef(o.refTo.getClassLoader().loadClass(ClassRecord.class.getName(), o.getStartPoint()), o.getStartPoint())).method("getAsClass").find().getRunTimeName());
            p.append("()");
            return true;
        });

        addGen(NewClass.class, (c, o, p, g) -> {
            InvokeGenerator ig = c.getInvokeGenerator(o.constructor);
            if (ig != null) {
                Invoke inv = new Invoke(o.constructor, null);
                inv.arguments = o.arguments;
                return ig.generate(c, inv, p);
            }
            ICodeGenerator icg = c.getGenerator(o.constructor.getParent());
            if (icg != null && icg != g)
                return icg.operation(c, o, p);
            //p.append(".");
            //p.append(o.getField().name);
            p.append("");
            g.operation(c, new StaticRef(o.constructor.getParent(), o.getStartPoint()), p);
            p.append(".").append("n").append(o.constructor.getRunTimeName()).append("(");
            boolean first = true;
            for (Value v : o.arguments) {
                if (!first)
                    p.append(",");
                g.operation(c, v, p);
                first = false;
            }
            p.append(")");
            return true;
            //throw new RuntimeException("new operator not suppported");
        });

        addGen(CodeExe.class, (c, o, p, g) -> {
            if (o.getExecute().getBlock() == null) {
                p.append("null");
                return false;
            }

            //System.out.println(o.getExecute().getParent().getRealName() + "=>" + o.getExecute().getDescription() + " : " + (o.getExecute().getStartPoint() == null ? "NULL POINT" : o.getExecute().getStartPoint().toStringLong()));

            //p.add(o.getExecute().getStartPoint(), o.getExecute().getParent().getRealName() + "." + o.getExecute().getDescription());
            if (o.getExecute() instanceof VMethod) {
                p.append("function(");
            } else {
                p.append("function(");
            }
            boolean first = true;
            for (VArgument a : o.getExecute().getArguments()) {
                if (!first)
                    p.append(",");
                //p.add(a.getStartPoint(), a.getRealName());
                p.append(a.getRuntimeName());
                first = false;
            }
            p.append(")");

            GenerationContext gc = new MainGenerationContext(o.getExecute().getParent(), c.getCompileModuls());
            ICodeGenerator cg = gc.getGenerator(o.getExecute());
            if (o.getExecute().generator != null) {
                p.append("{");
                cg.generateExecute(gc, o.getExecute(), p, c.getCompileModuls());
                p.append("}");
            } else
            /*
            if (cg != g) {
                return cg.operation(gc, o.getExecute().getBlock(), p);
            }
            */

                g.operation(gc, o.getExecute().getBlock(), p);

            return true;
        });

        addGen(SVar.class, (c, o, p, g) -> {
            p.append(o.getRuntimeName());
            return true;
        });

        addGen(Line.class, (c, o, p, g) -> {
            p.append("");
            boolean res = g.operation(c, o.getOperation(), p);
            p.append("");
            return res;
        });

        addGen(GetValue.class, (c, o, p, g) -> {
            if (o.getValue() instanceof SVar) {
                p.append("");
            } else p.append("");

            g.operation(c, o.getValue(), p);
            return true;
        });

        addGen(SetValue.class, (c, o, p, g) -> {

            if (o.getValue() instanceof SVar) {
                p.append("");
            } else p.append("");

            //p.add("", o.getPoint());
            g.operation(c, o.getValue(), p);

            switch (o.getAsType()) {
                case ASSIGN:
                    p.append("=");
                    break;
                case PLUS:
                    p.append("+=");
                    break;
                case MINUS:
                    p.append("-=");
                    break;
                default:
                    throw new RuntimeException("Unknown type " + o.getAsType());
            }

            g.operation(c, o.getNewValue(), p);

            return true;
        });

        addGen(SetField.class, (c, o, p, g) -> {
            VClass pp = o.getField().getParent();


            g.operation(c, o.getScope(), p);
            p.append(".").append(o.getField().getRuntimeName())
                    .append("=");
            g.operation(c, o.getValue(), p);
            return true;
        });

        addGen(VIf.class, (c, o, p, g) -> {
            p.append("if (");
            g.operation(c, o.value, p);
            p.append(")");
            if (o.thenBlock != null)
                g.operation(c, o.thenBlock, p);
            else {
                p.append("{}");
            }
            if (o.elseBlock != null) {
                p.append("else");
                g.operation(c, o.elseBlock, p);
            }
            return true;
        });
        addGen(Parens.class, (c, o, p, g) -> {
            p.append("(");
            g.operation(c, o.getValue(), p);
            p.append(")");
            return true;
        });
        addGen(VBinar.class, (c, o, p, g) -> {
            g.operation(c, o.getLeft(), p);

            switch (o.getBitType()) {
                case PLUS:
                    p.append("+");
                    break;
                case MINUS:
                    p.append("-");
                    break;
                case MUL:
                    p.append("*");
                    break;
                case EQ:
                    p.append("==");
                    break;
                case NE:
                    p.append("!=");
                    break;
                case LT://<
                    p.append("<");
                    break;
                case GE://>=
                    p.append(">=");
                    break;
                case GT://>
                    p.append(">");
                    break;
                case LE://<=
                    p.append("<=");
                    break;
                case OR://>
                    p.append("||");
                    break;
                case AND://>
                    p.append("&&");
                    break;
                case BITOR:
                    p.append("|");
                    break;
                case BITAND:
                    p.append("&");
                    break;
                case BITXOR:
                    p.append("^");
                    break;
                case MOD:
                    p.append("%");
                    break;
                case USR:
                    p.append(">>>");
                    break;
                case DIV:
                    p.append("/");
                    break;
                default:
                    throw new RuntimeException("Not support type " + o.getBitType());
            }

            g.operation(c, o.getRight(), p);
            return true;
        });

        addGen(VBlock.class, (c, o, p, g) -> {
            p.append("{");
            for (Operation op : o.getOperations()) {
                if (op == null)
                    continue;
                g.operation(c, op, p);
                if (op instanceof VBlock)
                    continue;
                if (op instanceof VIf)
                    continue;
                if (op instanceof ForLoop)
                    continue;
                if (op instanceof WhileLoop)
                    continue;
                if (op instanceof DoWhileLoop)
                    continue;
                p.append(";");
            }
            p.append("}");
            return true;
        });

        addGen(Throw.class, (c, o, p, g) -> {
            p.append("throw ");
            g.operation(c, o.getValue(), p);
            p.append("");
            return true;
        });

        addGen(WhileLoop.class, (c, o, p, g) -> {
            p.append("while (");
            g.operation(c, o.value, p);
            p.append(")");
            if (o.block == null)
                p.append("{}");
            else
                g.operation(c, o.block, p);
            return true;
        });

        addGen(DoWhileLoop.class, (c, o, p, g) -> {
            p.append("do");

            if (o.block == null)
                p.append("{}");
            else
                g.operation(c, o.block, p);

            p.append("while(");
            g.operation(c, o.value, p);
            p.append(")");
            return true;
        });

        addGen(Assign.class, (c, o, p, g) -> {
            g.operation(c, o.getVar(), p);
            switch (o.getAsType()) {
                case ASSIGN:
                    p.append("=");
                    break;
                case PLUS:
                    p.append("+=");
                    break;
                case MINUS:
                    p.append("-=");
                    break;
                case MUL:
                    p.append("*=");
                case DIV:
                    p.append("/=");
                    break;
                default:
                    throw new RuntimeException("Unknown type " + o.getAsType());
            }
            ;
            g.operation(c, o.getValue(), p);
            return true;
        });

        addGen(ForLoop.class, (c, o, p, g) -> {
            p.append("for(");
            g.operation(c, o.init, p);
            p.append(";");
            g.operation(c, o.value, p);
            p.append(";");
            g.operation(c, o.update, p);
            p.append(")");
            if (o.block != null) {
                g.operation(c, o.block, p);
            } else
                p.append("{}");
            return true;
        });

        addGen(Increment.class, (c, o, p, g) -> {
            switch (o.getIncType()) {
                case PRE_DEC:
                    p.append("--");
                    break;
                case PRE_INC:
                    p.append("++");
                    break;
                case NOT:
                    p.append("!");
                    break;
                case NEG:
                    p.append("-");
                    break;
                case POST_DEC:
                case POST_INC:
                    break;
                default:
                    throw new RuntimeException("Not supported " + o.getIncType());
            }

            g.operation(c, o.getValue(), p);

            switch (o.getIncType()) {
                case POST_DEC:
                    p.append("--");
                    break;
                case POST_INC:
                    p.append("++");
                    break;
            }
            return true;
        });

        addGen(ArrayGet.class, (c, o, p, g) -> {
            VMethod getMethod = o.getValue().getType().getMethod("get", null, o.getType().getClassLoader().loadClass("int", null));
            return g.operation(c, CodeBuilder.scope(o.getValue()).invoke(getMethod, null, null).arg(o.getIndex()).build(), p);
            /*
            Invoke inv = new Invoke(getMethod, o.getValue());
            inv.arguments.add(o.getIndex());
            g.operation(c, inv, p);
            return true;
            */
        });

        addGen(ArrayAssign.class, (c, o, p, g) -> {
            VMethod getMethod = o.getVar().getType().getMethod("set", null,
                    o.getType().getClassLoader().loadClass("int", null),
                    o.getType());
            Invoke inv = new Invoke(getMethod, o.getVar());
            inv.arguments.add(o.getIndexs());
            inv.arguments.add(o.getValue());
            g.operation(c, inv, p);
            return true;
        });

        addGen(Conditional.class, (c, o, p, g) -> {
            g.operation(c, o.getValue(), p);
            p.append("?");
            g.operation(c, o.getThenValue(), p);
            p.append(":");
            g.operation(c, o.getElseValue(), p);
            return true;
        });

        addGen(Lambda.class, (c, o, p, g) -> {
            ICodeGenerator icg = c.getGenerator(o.getType());
            if (icg != null && icg != g) {
                return icg.operation(c, o, p);
            }
            VClassLoader cl = o.getMethod().getParent().getClassLoader();
            VClass classClass = cl.loadClass(Class.class.getName(), null);
            VClass stringClass = cl.loadClass(String.class.getName(), null);
            VClass objectClass = cl.loadClass(Object.class.getName(), null);
            VMethod getLambdaMethod = classClass.getMethod("getLambda", null, stringClass, stringClass, objectClass, objectClass);
            g.operation(c, new StaticRef(o.getMethod().getParent(), null), p);
            p.append(".").append(getLambdaMethod.getRunTimeName()).append("(");
            g.operation(c, new Const(Integer.toString(o.hashCode()), stringClass), p);
            p.append(",");
            g.operation(c, new Const(o.getMethod().getRunTimeName(), stringClass), p);
            p.append(",function(");
            boolean first = true;
            for (VArgument a : o.arguments) {
                if (!first)
                    p.append(",");
                g.operation(c, a, p);
                first = false;
            }
            p.append("){");
            g.operation(c, o.getBlock(), p);
            p.append("}");
            p.append(",this");
            p.append(")");
            return true;
            //throw new RuntimeException("Lambda not supported");
        });

        /*
        addGen(Cast.class, (c, o, p, g) -> {

            if (true)
                throw new RuntimeException("Cast in runtime?");
            VClass clazz = o.getValue().getType();

            ICastAdapter ica = null;

            do {
                CastAdapter ca = (CastAdapter) clazz.getJavaClass().getAnnotation(CastAdapter.class);
                if (ca != null) {
                    try {
                        ica = ca.value().newInstance();
                        break;
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new CompileException(e);
                    }
                }
                clazz = clazz.extendsClass;
            } while (clazz != null);

            if (ica == null)
                throw new RuntimeException("Can't find cast adapter for " + o.getValue().getType().getJavaClass().getName());
            g.operation(c, ica.cast(o.getValue(), o.getType()), p);
            return true;
        });
        */

        addGen(Continue.class, (c, o, p, g) -> {
            p.append("continue");
            if (o.getLabel() != null) {
                p.append(" ").append(o.getLabel().getName());
            }
            return true;
        });

        addGen(Break.class, (c, o, p, g) -> {
            p.append("break");
            if (o.getLabel() != null) {
                p.append(" ").append(o.getLabel().getName());
            }
            return true;
        });

        addGen(NewArrayLen.class, (c, o, p, g) -> {
            if (o.sizes.size() > 1) {
                throw new RuntimeException("Not supported multiarray! yet!");
            }
            g.operation(c, new StaticRef(o.getType(), null), p);
            p.append(".n").append(ArrayClass.CONSTRUCTOR).append("(");
            g.operation(c, o.sizes.get(0), p);
            p.append(")");
            return true;
        });

        addGen(NewArrayItems.class, (c, o, p, g) -> {
            VClass classArrayBuilder = o.getType().getClassLoader().loadClass(ArrayBuilder.class.getName(), o.getStartPoint());
            VMethod methodGet = classArrayBuilder.getMethodByName("create").get(0);

            return g.operation(c, CodeBuilder.scopeStatic((classArrayBuilder)).invoke(methodGet, null, o.getStartPoint()).arg(
                    new ClassRecordRef(o.getType(), null)
                    ).arg(o).build()
                    , p);
            /*
            g.operation(c, new Invoke(methodGet, new ClassRecordRef(classArrayBuilder, null))
                    .addArg(new StaticRef(o.getType(), null))
                    .addArg(o), p);

            return true;
            */
        });

        addGen(Try.class, (c, o, p, g) -> {
            p.append("try");
            g.operation(c, o.block, p);
            if (!o.catchs.isEmpty()) {
                VClass errorClass = c.getCurrentClass().getClassLoader().loadClass(Throwable.class.getName(), o.getStartPoint());
                VClass objectClass = c.getCurrentClass().getClassLoader().loadClass(Object.class.getName(), o.getStartPoint());

                VMethod convertMethod = errorClass.getMethod("jsErrorConvert", o.getStartPoint(), objectClass);

                SVar evar = new SVar(c.genLocalName(), errorClass, o.block);
                String lab = c.genLocalName();
                p.append("catch(").append(evar.getRuntimeName()).append("){");
                p.append("console.error(").append(evar.getRuntimeName()).append(".stack);");
                g.operation(c, new Assign(evar, new Invoke(convertMethod, new StaticRef(errorClass, null)).addArg(evar), evar.getType(), Assign.AsType.ASSIGN), p);
                p.append(";");
                p.append(lab).append(":{");
                for (Try.Catch ca : o.catchs) {
                    boolean first = true;
                    p.append("if (");
                    for (VClass cl : ca.classes) {
                        if (!first)
                            p.append("||");
                        g.operation(c, new InstanceOf(evar, cl, null), p);
                        first = false;
                    }
                    p.append(") {");
                    ca.getDeclareVar().getVar().setRuntimeName(evar.getRuntimeName());
                    g.operation(c, ca.block, p);
                    p.append("break ").append(lab).append(";}");
                }

                p.append("throw ").append(evar.getRuntimeName()).append("}}");
            } else {
                p.append("catch(e){throw e;}");
            }
            return true;
        });

        addGen(InstanceOf.class, (c, o, p, g) -> {
            ICodeGenerator cg = c.getGenerator(o.getClazz());
            if (cg != null && cg != g)
                return cg.operation(c, o, p);

            VClass objectClass = o.getClazz().getClassLoader().loadClass(Object.class.getName(), o.getStartPoint());
            return g.operation(c,
                    CodeBuilder.scopeClass(o.getClazz()).method("isInstance").arg(objectClass).invoke(null, o.getStartPoint()).arg(o.getValue()).build()
                    , p);

            /*
            VClass classClass = o.getClazz().getClassLoader().loadClass(Class.class.getName(), o.getPoint());
            VMethod method = classClass.getMethod("isInstance", o.getPoint(), o.getClazz().getClassLoader().loadClass(Object.class.getName(), o.getPoint()));
            return g.operation(c, new Invoke(method, new StaticRef(o.getClazz(), null), o.getPoint()).addArg(o.getValue()), p);
            */
        });

        addGen(Switch.class, (c, o, p, g) -> {
            p.append("switch(");
            g.operation(c, o.getValue(), p);
            p.append("){");
            for (Switch.Case ca : o.getCases()) {
                if (ca.value == null)
                    p.append("default:");
                else {
                    p.append("case ");
                    g.operation(c, ca.value, p);
                    p.append(":");
                }
                g.operation(c, ca.block, p);
            }
            p.append("}");
            return true;
        });

        /*
        addGen(ForEach.class, (c,o,p,g)->{
            VClass iter = c.getCurrentClass().getClassLoader().loadClass(Iterable.class.getName());
            if (o.getValue().getType().isParent(iter)) {
                VClass it = c.getCurrentClass().getClassLoader().loadClass(Iterator.class.getName());
                SVar var = new SVar(it, null);
                var.name = "t"+Integer.toString(var.hashCode(), Character.MAX_RADIX);
                DeclareVar dv = new DeclareVar(var);
            }
            return true;
        });
        */
    }

    public static <T> void addGen(Class<? extends T> clazz, Gen<? extends T> gen) {
        generators.put(clazz, gen);
    }

    protected void generateMethodStart(GenerationContext ctx, VExecute execute, Outbuffer ps) {
        throw new RuntimeException("Not supported");
    }

    protected void generateMethodEnd(GenerationContext ctx, VExecute execute, Outbuffer ps) {
        throw new RuntimeException("Not supported");
    }

    protected void generateMethodNull(GenerationContext ctx, VExecute execute, Outbuffer ps) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public void generateExecute(GenerationContext context, VExecute execute, Outbuffer ps, CompileModuls moduls) throws CompileException {
        try {
            if (execute instanceof VConstructor) {
                VConstructor c = (VConstructor) execute;
                if (c.parentConstructorInvoke != null) {
                    operation(context, c.parentConstructorInvoke, ps);
                    ps.append(";");
                }
                for (VField f : c.getParent().getLocalFields()) {
                    if (f.isStatic())
                        continue;
                    ps.append("this.").append(f.getRuntimeName()).append("=");
                    if (f.init == null)
                        ps.append("null");
                    else
                        operation(context, f.init, ps);
                    ps.append(";");
                }
            }

            for (Operation op : execute.getBlock().getOperations()) {
                operation(context, op, ps);
                ps.append(";");
            }
        } catch (Throwable e) {
            throw new CompileException("Can't generate " + execute.getParent().getRealName() + "::" + execute.alias, e, null);
        }
    }

    protected void addGenerated(VClass clazz) {
        generatedClasses.add(clazz);
    }

    protected boolean isGenerated(VClass clazz) {
        return generatedClasses.contains(clazz);
    }

    /*
    @Override
    public boolean member(GenerationContext ctx, Member op, PrintStream ps) throws CompileException {
        throw new RuntimeException("Not supported yet " + op.getClass().getName());
    }
    */

    @Override
    public void generateClass(GenerationContext context, CompileModuls.ClassRecord record, Outbuffer ps) throws CompileException {
        throw new RuntimeException("Not supported yet");
    }

    @Override
    public boolean operation(GenerationContext context, Operation op, Outbuffer out) throws CompileException {
        if (op == null)
            return false;
        Gen g = generators.get(op.getClass());
        if (g != null) {
            return g.gen(context, op, out, this);
        }
        throw new RuntimeException("Not supported yet " + op.getClass().getName());
    }

    private interface Gen<T> {
        public boolean gen(GenerationContext ctx, T op, Outbuffer ps, ICodeGenerator gen) throws CompileException;
    }
}
