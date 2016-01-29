package org.tlsys.twt;

import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.InvokeGen;
import org.tlsys.twt.classes.ClassStorage;
import org.tlsys.twt.classes.TypeProvider;
import org.tlsys.twt.rt.java.lang.TClassLoader;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
            if (o.getValue() instanceof String) {
                p.append("\"").append(o.getValue().toString()).append("\"");
                return true;
            }
            p.append(o.getValue().toString());
            return true;
        });

        addGen(Return.class, (c, o, p, g) -> {
            p.append("return");
            if (o.getValue() != null) {
                p.append(" ");
                g.operation(c, o.getValue(), p);
            }
            return true;
        });

        addGen(This.class, (c, o, p, g) -> {
            if (o.getType() != c.getCurrentClass())
                throw new RuntimeException("Not support other this type");
            p.append("this");
            return true;
        });

        addGen(Invoke.class, (c, o, p, g) -> {
            if ("getType".equals(o.getMethod().alias))
                System.out.println("123");
            InvokeGenerator icg = c.getInvokeGenerator(o.getMethod());
            if (icg != null)
                return icg.generate(c, o, p);

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

            if (o.getSelf() instanceof This) {//вызов конструктора
                This self = (This) o.getSelf();

                if (o.getSelf().getType() != c.getCurrentClass()) {//чужого
                    c.getGenerator(self.getType()).operation(c, new StaticRef(self.getType()), p);
                    p.append(".");
                    if (!o.getMethod().isStatic())
                        p.append("prototype.");
                    p.append(o.getMethod().name);
                    p.append(".apply(this, ");
                    printArg.test(false);
                    p.append(")");
                    return true;
                }
            }
            g.operation(c, o.getSelf(), p);
            p.append(".");
            p.append(o.getMethod().name);
            p.append("(");
            printArg.test(true);
            p.append(")");
            return true;
        });

        addGen(VArgument.class, (c, o, p, g) -> {
            p.append(o.name);
            return true;
        });
        addGen(DeclareVar.class, (c, o, p, g) -> {
            p.append("var ").append(o.getVar().name);
            if (o.init != null) {
                p.append("=");
                g.operation(c, o.init, p);
            }
            return true;
        });
        addGen(GetField.class, (c, o, p, g) -> {
            g.operation(c, o.getScope(), p);
            p.append(".");
            p.append(o.getField().name);
            return true;
        });

        addGen(StaticRef.class, (c, o, p, g) -> {
            VClass classClass = o.getType().getClassLoader().loadClass(Class.class.getName());
            ICodeGenerator icg = c.getGenerator(o.getType());
            if (icg != null && icg != g)
                return icg.operation(c, o, p);
            if (o.getType() instanceof ArrayClass) {
                VClass cur = o.getType();
                int level = 0;
                do {
                    ++level;
                    cur = ((ArrayClass) cur).getComponent();
                } while (cur instanceof ArrayClass);
                Value lastScope = new StaticRef(cur);
                while (level > 0) {
                    Invoke inv = new Invoke(classClass.getMethod("getArrayClass"), lastScope);
                    lastScope = inv;
                    --level;
                }
                return g.operation(c, lastScope, p);
            }
            VMethod getMethod = o.getType().getClassLoader().loadClass(ClassStorage.class.getName()).getMethod("get", o.getType().getClassLoader().loadClass(Object.class.getName()));
            p.append(Generator.storage.name).append(".").append(getMethod.name).append("(").append(Generator.storage.name).append(".").append(o.getType().fullName).append(")");
            //throw new RuntimeException("Class ref not supported yet");
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
            g.operation(c, new StaticRef(o.constructor.getParent()), p);
            p.append(".n").append(o.constructor.name).append("(");
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

        addGen(DeclareClass.class, (c, o, p, g) -> {
            VClass stringClass = c.getCurrentClass().getClassLoader().loadClass(String.class.getName());
            VClass classClass = c.getCurrentClass().getClassLoader().loadClass(Class.class.getName());
            VMethod addClassMethod = o.getClassLoaderVar().getType().getMethod("addClass", stringClass, classClass);
            Invoke inv = new Invoke(addClassMethod, o.getClassLoaderVar());
            VClass clazz = o.getType();

            NewClass classInit = new NewClass(classClass.constructors.get(0));
            classInit.arguments.add(new Const(clazz.alias, stringClass));

            VClass fieldClass = clazz.getClassLoader().loadClass(Field.class.getName());

            inv.arguments.add(new Const(clazz.fullName, stringClass));
            inv.arguments.add(classInit);
            if (g.operation(c, inv, p)) {
                p.append(";\n");
                return true;
            }
            return false;
        });

        addGen(SVar.class, (c, o, p, g) -> {
            p.append(o.name);
            return true;
        });

        addGen(SetField.class, (c, o, p, g) -> {
            g.operation(c, o.getScope(), p);
            p.append(".").append(o.getField().name).append("=");
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
                case EQ:
                    p.append("==");
                    break;
                case NE:
                    p.append("!=");
                    break;
                case LT://<
                    p.append("<");
                    break;
                case GE://>
                    p.append(">");
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
                default:
                    throw new RuntimeException("Not support type " + o.getBitType());
            }

            g.operation(c, o.getRight(), p);
            return true;
        });

        addGen(VBlock.class, (c, o, p, g) -> {
            p.append("{");
            for (Operation op : o.operations) {
                g.operation(c, op, p);
                if (op instanceof VBlock)
                    continue;
                if (op instanceof VIf)
                    continue;
                if (op instanceof ForEach)
                    continue;
                if (op instanceof ForLoop)
                    continue;
                if (op instanceof WhileLoop)
                    continue;
                p.append(";");
            }
            p.append("}");
            return true;
        });

        addGen(Throw.class, (c, o, p, g) -> {
            p.append("throw ");
            g.operation(c, o.getValue(), p);
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
            VMethod getMethod = o.getType().getMethod("get", o.getType().getClassLoader().loadClass("int"));
            Invoke inv = new Invoke(getMethod, o.getIndex());
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
            if (icg != null && icg != g)
                return icg.operation(c, o, p);
            throw new RuntimeException("Lambda not supported");
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
            g.operation(c, ica.cast(c, o.getValue(), o.getType()), p);
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

    public static <T> void addGen(Class<T> clazz, Gen<T> gen) {
        generators.put(clazz, gen);
    }

    protected void generateMethodStart(GenerationContext ctx, VExecute execute, PrintStream ps) {
        throw new RuntimeException("Not supported");
    }

    protected void generateMethodEnd(GenerationContext ctx, VExecute execute, PrintStream ps) {
        throw new RuntimeException("Not supported");
    }

    protected void generateMethodNull(GenerationContext ctx, VExecute execute, PrintStream ps) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public void generateExecute(GenerationContext context, VExecute execute, PrintStream ps) throws CompileException {
        operation(context, execute.block, ps);
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
    public void generateClass(GenerationContext context, CompileModuls.ClassRecord record, PrintStream ps) throws CompileException {
        throw new RuntimeException("Not supported yet");
    }

    @Override
    public boolean operation(GenerationContext context, Operation op, PrintStream out) throws CompileException {
        Gen g = generators.get(op.getClass());
        if (g != null) {
            return g.gen(context, op, out, this);
        }
        throw new RuntimeException("Not supported yet " + op.getClass().getName());
    }

    private interface Gen<T> {
        public boolean gen(GenerationContext ctx, T op, PrintStream ps, ICodeGenerator gen) throws CompileException;
    }
}
