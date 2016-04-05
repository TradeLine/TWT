package org.tlsys.twt;

import org.tlsys.Outbuffer;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;
import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.classes.ArrayBuilder;
import org.tlsys.twt.classes.ClassStorage;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Predicate;

public class DefaultGenerator implements ICodeGenerator {


    private static final HashSet<VClass> generatedClasses = new HashSet<>();
    private static HashMap<Class, Gen> generators = new HashMap<>();

    static {
        addGen(Const.class, (c, o, p, g) -> {
            if (o.getValue() == null) {
                p.add("null", o.getPoint());
                return true;
            }
            if (o.getValue() instanceof String) {
                p.add("'", o.getPoint()).append(o.getValue().toString().replace("'", "\\'")).append("'");
                return true;
            }
            p.add(o.getValue().toString(), o.getPoint());
            return true;
        });

        addGen(Return.class, (c, o, p, g) -> {
            p.add("return", o.getPoint());
            if (o.getValue() != null) {
                p.append(" ");
                g.operation(c, o.getValue(), p);
            }
            return true;
        });

        addGen(This.class, (c, o, p, g) -> {
            if (o.getType() != c.getCurrentClass()) {
                Optional<VClass> cl = c.getCurrentClass().getDependencyParent();
                /*
                if (cl.isPresent() && cl.get() == o.getType()) {
                    System.out.println("getting this of parent " + c.getCurrentClass().hashCode() + " " + c.getCurrentClass());
                    return g.operation(c, TypeUtil.getParentThis(c.getCurrentClass()), p);
                }
                */
                throw new RuntimeException("Not support other this type. Current " + c.getCurrentClass() + ", this=" + o.getType());
            }
            p.add("this", o.getPoint());
            return true;
        });

        addGen(Invoke.class, (c, o, p, g) -> {
            InvokeGenerator icg = c.getInvokeGenerator(o.getMethod());
            if (icg != null) {
                return icg.generate(c, o, p);
            }

            ICodeGenerator icg2 = c.getGenerator(o.getMethod());
            if (icg != null && icg != g)
                return icg.generate(c, o, p);

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

            /*
            if (o.getMethod() instanceof VConstructor) {
                if (o.getScope() instanceof This) {
                    if (o.getType() != o.getMethod().getParent())
                        System.out.println("123");
                }

            }
            */

            if (o.getScope() instanceof This) {//вызов конструктора
                This self = (This) o.getScope();

                if (o.getMethod().getParent() != o.getScope().getType() || o.getMethod() instanceof VConstructor) {//чужого
                    int pos = p.getCurrent();
                    c.getGenerator(self.getType()).operation(c, new StaticRef(o.getMethod().getParent(), o.getPoint()), p);
                    p.append(".");

                    if (!o.getMethod().isStatic())
                        p.append(o.getScope().getType().getClassLoader().loadClass(Class.class.getName()).getMethod("getJsClass").getRunTimeName()).append("()").append(".prototype.");
                    p.add(o.getMethod().getRunTimeName(), o.getPoint(), pos);
                    p.append(".call(this");
                    printArg.test(false);
                    p.add(")", o.getPoint(), pos);
                    return true;
                }
            }
            int pos = p.getCurrent();
            g.operation(c, o.getScope(), p);
            p.append(".");
            if (o.getMethod() instanceof VMethod) {
                VMethod m = (VMethod) o.getMethod();
                p.add(o.getMethod().getRunTimeName(), o.getPoint(), pos, m.getRunTimeName());
            } else
                p.add(o.getMethod().getRunTimeName(), o.getPoint(), pos);
            p.add("(", o.getPoint());
            printArg.test(true);
            p.add(")", o.getPoint());
            return true;
        });

        addGen(VArgument.class, (c, o, p, g) -> {
            p.add(o.getRuntimeName(), o.getPoint(), o.getRealName());
            return true;
        });

        addGen(DeclareVar.class, (c, o, p, g) -> {
            p.add("var ", o.getPoint(), o.getVar().getRealName()).append(o.getVar().getRuntimeName());
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
            p.add(o.getField().getRuntimeName(), o.getPoint(), pos, o.getField().getRealName());
            //p.append();
            return true;
        });

        //ClassRef

        Gen<Value> classRef = (c, o, p, g) -> {
            VClass cl = null;
            if (o instanceof ClassRef)
                cl = ((ClassRef)o).refTo;
            else
                cl = o.getType();

            VClass classClass = o.getType().getClassLoader().loadClass(Class.class.getName());
            ICodeGenerator icg = c.getGenerator(cl);
            if (icg != null && icg != g)
                return icg.operation(c, o, p);
            if (o.getType() instanceof ArrayClass) {
                VClass cur = o.getType();
                int level = 0;
                do {
                    ++level;
                    cur = ((ArrayClass) cur).getComponent();
                } while (cur instanceof ArrayClass);
                SourcePoint sp = null;

                if (o instanceof StaticRef) {
                    StaticRef sr = (StaticRef) o;
                    sp = sr.getPoint();
                }

                if (o instanceof ClassRef) {
                    ClassRef sr = (ClassRef) o;
                    sp = sr.getPoint();
                }

                Value lastScope = new StaticRef(cur, sp);
                while (level > 0) {
                    Invoke inv = new Invoke(classClass.getMethod("getArrayClass"), lastScope);
                    lastScope = inv;
                    --level;
                }
                return g.operation(c, lastScope, p);
            }
            VMethod getMethod = o.getType().getClassLoader().loadClass(ClassStorage.class.getName()).getMethod("get", o.getType().getClassLoader().loadClass(Object.class.getName()));

            SourcePoint sp = null;
            if (o instanceof StaticRef) {
                StaticRef sr = (StaticRef) o;
                sp = sr.getPoint();
                //p.add("", sr.getPoint(), sr.getType().getRealName());
            }

            if (o instanceof ClassRef) {
                ClassRef sr = (ClassRef) o;
                sp = sr.getPoint();
                //p.add("", sr.getPoint(), sr.getType().getRealName());
            }

            p.add(Generator.storage.getRuntimeName(), sp).append(".").add(getMethod.getRunTimeName(), sp).append("(").add(Generator.storage.getRuntimeName(), sp).append(".").add(cl.fullName, sp, cl.getRealName()).add(")", sp);
            //throw new RuntimeException("Class ref not supported yet");
            return true;
        };

        addGen(StaticRef.class, classRef);
        addGen(ClassRef.class, classRef);

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
            p.add("", o.getPoint());
            g.operation(c, new StaticRef(o.constructor.getParent(), null), p);
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

        addGen(CodeExe.class, (c,o,p,g)->{
            if (o.getExecute().getBlock() == null) {
                p.append("null");
                return false;
            }
            if (o.getExecute() instanceof VMethod)
                p.add("function(", o.getExecute().getPoint(), ((VMethod)o.getExecute()).getRealName());
            else
                p.add("function(", o.getExecute().getPoint());
            boolean first = true;
            for (VArgument a : o.getExecute().getArguments()) {
                if (!first)
                    p.append(",");
                p.add(a.getRuntimeName(), a.getPoint(), a.getRealName());
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

        /*
        addGen(DeclareClass.class, (c, o, p, g) -> {
            VClass stringClass = c.getCurrentClass().getClassLoader().loadClass(String.class.getName());
            VClass classClass = c.getCurrentClass().getClassLoader().loadClass(Class.class.getName());
            VMethod addClassMethod = o.getClassLoaderVar().getType().getMethod("addClass", stringClass, classClass);
            Invoke inv = new Invoke(addClassMethod, o.getClassLoaderVar());
            VClass clazz = o.getType();

            NewClass classInit = new NewClass(classClass.constructors.get(0), sourcePoint);
            classInit.arguments.add(new Const(clazz.alias, stringClass));

            VClass fieldClass = clazz.getClassLoader().loadClass(Field.class.getName());

            inv.arguments.add(new Const(clazz.fullName, stringClass));
            inv.arguments.add(classInit);
            if (g.operation(c, inv, p)) {
                p.append(";");
                return true;
            }
            return false;
        });
        */

        addGen(SVar.class, (c, o, p, g) -> {
            p.append(o.getRuntimeName());
            return true;
        });

        addGen(Line.class, (c, o, p, g) -> {
            System.out.println("LINE!!!");
            p.add("", o.getStartPoint());
            boolean res = g.operation(c, o.getOperation(), p);
            p.add("", o.getEndPoint());
            return res;
        });

        addGen(GetValue.class, (c, o, p, g) -> {
            if (o.getValue() instanceof SVar) {
                p.add("", o.getPoint(), ((SVar) o.getValue()).getRealName());
            } else p.add("", o.getPoint());

            g.operation(c, o.getValue(), p);
            return true;
        });

        addGen(SetValue.class, (c, o, p, g) -> {

            if (o.getValue() instanceof SVar) {
                p.add("", o.getPoint(), ((SVar) o.getValue()).getRealName());
            } else p.add("", o.getPoint());

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
            p.append(".").add(o.getField().getRuntimeName(), o.getPoint(), o.getField().getRealName())
                    .add("=", o.getOpPoint());
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
            p.add("(", o.getPoint());
            g.operation(c, o.getValue(), p);
            p.append(")");
            return true;
        });
        addGen(VBinar.class, (c, o, p, g) -> {
            g.operation(c, o.getLeft(), p);

            switch (o.getBitType()) {
                case PLUS:
                    p.add("+", o.getPoint());
                    break;
                case MINUS:
                    p.add("-", o.getPoint());
                    break;
                case MUL:
                    p.add("*", o.getPoint());
                    break;
                case EQ:
                    p.add("==", o.getPoint());
                    break;
                case NE:
                    p.add("!=", o.getPoint());
                    break;
                case LT://<
                    p.add("<", o.getPoint());
                    break;
                case GE://>
                    p.add(">", o.getPoint());
                    break;
                case GT://>
                    p.add(">", o.getPoint());
                    break;
                case LE://<=
                    p.add("<=", o.getPoint());
                    break;
                case OR://>
                    p.add("||", o.getPoint());
                    break;
                case AND://>
                    p.add("&&", o.getPoint());
                    break;
                case BITOR:
                    p.add("|", o.getPoint());
                    break;
                case BITAND:
                    p.add("&", o.getPoint());
                    break;
                case BITXOR:
                    p.add("^", o.getPoint());
                    break;
                case MOD:
                    p.add("%", o.getPoint());
                    break;
                case USR:
                    p.add(">>>", o.getPoint());
                    break;
                case DIV:
                    p.add("/", o.getPoint());
                    break;
                default:
                    throw new RuntimeException("Not support type " + o.getBitType());
            }

            g.operation(c, o.getRight(), p);
            return true;
        });

        addGen(VBlock.class, (c, o, p, g) -> {
            p.add("{", o.getStartPoint());
            for (Operation op : o.getOperations()) {
                if (op == null)
                    continue;
                g.operation(c, op, p);
                p.append("/*->" + op + "*/");
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
            p.add("}", o.getEndPoint() != null?o.getEndPoint():o.getStartPoint());
            return true;
        });

        addGen(Throw.class, (c, o, p, g) -> {
            p.add("throw ", o.getPoint());
            g.operation(c, o.getValue(), p);
            return true;
        });

        addGen(WhileLoop.class, (c, o, p, g) -> {
            p.add("while (", o.getPoint());
            g.operation(c, o.value, p);
            p.add(")", o.getPoint());
            if (o.block == null)
                p.append("{}");
            else
                g.operation(c, o.block, p);
            return true;
        });

        addGen(DoWhileLoop.class, (c, o, p, g) -> {
            p.add("do", o.getPoint());

            if (o.block == null)
                p.append("{}");
            else
                g.operation(c, o.block, p);

            p.add("while(", o.getPoint());
            g.operation(c, o.value, p);
            p.add(")", o.getPoint());
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
                default:
                    throw new RuntimeException("Unknown type " + o.getAsType());
            }
            ;
            g.operation(c, o.getValue(), p);
            return true;
        });

        addGen(ForLoop.class, (c, o, p, g) -> {
            p.add("for(", o.getPoint());
            g.operation(c, o.init, p);
            p.append(";");
            g.operation(c, o.value, p);
            p.append(";");
            g.operation(c, o.update, p);
            p.add(")", o.getPoint());
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
                    p.append("");
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
            VMethod getMethod = o.getValue().getType().getMethod("get", o.getType().getClassLoader().loadClass("int"));
            Invoke inv = new Invoke(getMethod, o.getValue());
            inv.arguments.add(o.getIndex());
            g.operation(c, inv, p);
            return true;
        });

        addGen(ArrayAssign.class, (c, o, p, g) -> {
            VMethod getMethod = o.getVar().getType().getMethod("set",
                    o.getType().getClassLoader().loadClass("int"),
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
            VClass classClass = cl.loadClass(Class.class.getName());
            VClass stringClass = cl.loadClass(String.class.getName());
            VClass objectClass = cl.loadClass(Object.class.getName());
            VMethod getLambdaMethod = classClass.getMethod("getLambda", stringClass, stringClass, objectClass, objectClass);
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

        addGen(Cast.class, (c, o, p, g) -> {
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

        addGen(Continue.class, (c, o, p, g) -> {
            p.add("continue", o.getPoint());
            if (o.getLabel() != null) {
                p.append(" ").append(o.getLabel().getName());
            }
            return true;
        });

        addGen(Break.class, (c, o, p, g) -> {
            p.add("break", o.getPoint());
            if (o.getLabel() != null) {
                p.append(" ").append(o.getLabel().getName());
            }
            return true;
        });

        addGen(NewArrayLen.class, (c,o,p,g)->{
            if (o.sizes.size() > 1) {
                throw new RuntimeException("Not supported multiarray! yet!");
            }
            g.operation(c, new StaticRef(o.getType(), null), p);
            p.append(".n").append(ArrayClass.CONSTRUCTOR).append("(");
            g.operation(c, o.sizes.get(0), p);
            p.append(")");
            return true;
        });

        addGen(NewArrayItems.class, (c,o,p,g)->{
            VClass classArrayBuilder = o.getType().getClassLoader().loadClass(ArrayBuilder.class.getName());
            VMethod methodGet = classArrayBuilder.getMethodByName("create").get(0);
            g.operation(c, new Invoke(methodGet, new StaticRef(classArrayBuilder, null))
                    .addArg(new StaticRef(o.getType(), null))
            .addArg(o), p);
            return true;
        });

        addGen(Try.class, (c,o,p,g)->{
            p.add("try", o.getPoint());
            g.operation(c, o.block, p);
            if (!o.catchs.isEmpty()) {
                VClass errorClass = c.getCurrentClass().getClassLoader().loadClass(Throwable.class.getName());
                VClass objectClass = c.getCurrentClass().getClassLoader().loadClass(Object.class.getName());

                VMethod convertMethod = errorClass.getMethod("jsErrorConvert", objectClass);

                SVar evar = new SVar(c.genLocalName(), errorClass, o.block);
                String lab = c.genLocalName();
                p.append("catch(").append(evar.getRuntimeName()).append("){");
                p.append("console.error(").append(evar.getRuntimeName()).append(".stack);");
                g.operation(c, new Assign(evar, new Invoke(convertMethod, new StaticRef(errorClass, null)).addArg(evar), evar.getType(), Assign.AsType.ASSIGN), p);
                p.append(";");
                p.append(lab).append(":{");
                for (Try.Catch ca : o.catchs) {
                    boolean first = true;
                    p.add("if (", ca.getPoint());
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

        addGen(InstanceOf.class, (c,o,p,g)->{
            ICodeGenerator cg = c.getGenerator(o.getClazz());
            if (cg != null && cg != g)
                return cg.operation(c, o, p);
            VClass classClass = o.getClazz().getClassLoader().loadClass(Class.class.getName());
            VMethod method = classClass.getMethod("isInstance", o.getClazz().getClassLoader().loadClass(Object.class.getName()));
            return g.operation(c, new Invoke(method, new StaticRef(o.getClazz(), null), o.getPoint()).addArg(o.getValue()), p);
        });

        addGen(Switch.class, (c,o,p,g)->{
            p.add("switch(", o.getPoint());
            g.operation(c, o.getValue(),p);
            p.append("){");
            for (Switch.Case ca : o.getCases()) {
                if (ca.value == null)
                    p.add("default:", ca.getPoint());
                else {
                    p.add("case ", ca.getPoint());
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
            throw new CompileException("Can't generate " + execute.getParent().getRealName() + "::"+execute.alias, e);
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
