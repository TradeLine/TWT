package org.tlsys.twt;

import org.tlsys.CodeBuilder;
import org.tlsys.MethodSelectorUtils;
import org.tlsys.Outbuffer;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.classes.ArrayBuilder;
import org.tlsys.twt.classes.ClassRecord;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class DefaultGenerator implements ICodeGenerator, OperationVisiter {


    private static final HashSet<VClass> generatedClasses = new HashSet<>();

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

    @Override
    public void generateClass(GenerationContext context, CompileModuls.ClassRecord record, Outbuffer ps) throws CompileException {
        throw new RuntimeException("Not supported yet");
    }

    @Override
    public boolean operation(GenerationContext context, Operation op, Outbuffer out) throws CompileException {

        GenerationContext cc = c;
        c = context;
        p = out;

        if (op == null)
            return false;

        boolean res = op.accept(this);
        c = cc;
        return res;
    }

    @Override
    public boolean visit(Operation node) throws CompileException {
        if (node.getClass() == DeclareVar.class) {
            DeclareVar o = (DeclareVar) node;
            p.add(o.getPoint(), o.getVar().getRealName());
            p.append("var ").append(o.getVar().getRuntimeName());
            if (o.init != null) {
                p.append("=");
                o.init.accept(this);
            }
            return true;
        }
        throw new RuntimeException("Unknown node! " + node);
    }

    @Override
    public boolean visit(Line o) throws CompileException {
        if (o.getOperation() == null)
            return false;
        return o.getOperation().accept(this);
    }

    @Override
    public boolean visit(Value node) throws CompileException {
        if (node.getClass() == ClassRecordRef.class) {
            ClassRecordRef o = (ClassRecordRef) node;
            ICodeGenerator icg = c.getGenerator(o.getType());
            if (icg != null && icg != this)
                return icg.operation(c, o, p);

            if (o.getToClass() instanceof ArrayClass) {
                ArrayClass ac = (ArrayClass) o.getToClass();
                return
                        CodeBuilder.scope(new ClassRecordRef(ac.getComponent(), o.getStartPoint()))
                                .method("getArrayClassRecord")
                                .invoke(o.getStartPoint(), null)
                                .build().accept(this);

            }

            p.append(Generator.storage.getRuntimeName());
            p.append(".");
            p.append(o.getToClass().fullName);
            return true;
        }
        if (node.getClass() == VArgument.class) {
            VArgument o = (VArgument) node;
            p.append(o.getRuntimeName());
            return true;
        }

        if (node.getClass() == SVar.class) {
            SVar o = (SVar) node;
            p.append(o.getRuntimeName());
            return true;
        }

        if (node.getClass() == CodeExe.class) {
            CodeExe o = (CodeExe) node;
            if (o.getExecute().getBlock() == null) {
                p.append("null");
                return false;
            }

            if (o.getExecute() instanceof VMethod) {
                p.append("function(");
            } else {
                p.append("function(");
            }
            boolean first = true;
            for (VArgument a : o.getExecute().getArguments()) {
                if (!first)
                    p.append(",");
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
            } else {
                DefaultGenerator g;
                if (c.getCurrentClass() != o.getExecute().getParent()) {
                    g = new DefaultGenerator();
                    g.c = gc;
                    g.p = p;
                } else
                    g = this;
                o.getExecute().getBlock().accept(g);
            }

            return true;
        }

        if (node.getClass() == Lambda.class) {


            Lambda o = (Lambda) node;
            ICodeGenerator icg = c.getGenerator(o.getType());
            if (icg != null && icg != this) {
                return icg.operation(c, o, p);
            }
            throw new RuntimeException("Lambda not supported!");
            /*
            VClassLoader cl = o.getMethod().getParent().getClassLoader();
            VClass classClass = cl.loadClass(Class.class.getName(), null);
            VClass stringClass = cl.loadClass(String.class.getName(), null);
            VClass objectClass = cl.loadClass(Object.class.getName(), null);
            VMethod getLambdaMethod = MethodSelectorUtils.getMethod(classClass, "getLambda", null, stringClass, stringClass, objectClass, objectClass);
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
            */

        }

        throw new RuntimeException("Unknown value! " + node.getClass().getName());
    }

    @Override
    public boolean visit(VBinar o) throws CompileException {
        o.getLeft().accept(this);

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

        o.getRight().accept(this);
        return true;
    }

    @Override
    public boolean visit(Return o) throws CompileException {
        p.add(o.getStartPoint());
        p.append("return");
        if (o.getValue() != null) {
            p.append(" ");
            o.getValue().accept(this);
        }
        return true;
    }

    @Override
    public boolean visit(WhileLoop o) throws CompileException {
        p.append("while (");
        o.value.accept(this);
        p.append(")");
        if (o.block == null)
            p.append("{}");
        else
            o.block.accept(this);
        return true;
    }

    @Override
    public boolean visit(Try o) throws CompileException {
        p.append("try");
        o.block.accept(this);
        if (!o.catchs.isEmpty()) {
            VClass errorClass = c.getCurrentClass().getClassLoader().loadClass(Throwable.class.getName(), o.getStartPoint());
            VClass objectClass = c.getCurrentClass().getClassLoader().loadClass(Object.class.getName(), o.getStartPoint());

            VMethod convertMethod = MethodSelectorUtils.getMethod(errorClass, "jsErrorConvert", o.getStartPoint(), objectClass);

            SVar evar = new SVar(c.genLocalName(), errorClass, o.block);
            String lab = c.genLocalName();
            p.append("catch(").append(evar.getRuntimeName()).append("){");
            p.append("console.error(").append(evar.getRuntimeName()).append(".stack);");
            new Assign(evar, new Invoke(convertMethod, new StaticRef(errorClass, null)).addArg(evar), evar.getType(), Assign.AsType.ASSIGN).accept(this);
            p.append(";");
            p.append(lab).append(":{");
            for (Try.Catch ca : o.catchs) {
                boolean first = true;
                p.append("if (");
                for (VClass cl : ca.classes) {
                    if (!first)
                        p.append("||");
                    new InstanceOf(evar, cl, null).accept(this);
                    first = false;
                }
                p.append(") {");
                ca.getDeclareVar().getVar().setRuntimeName(evar.getRuntimeName());
                ca.block.accept(this);
                p.append("break ").append(lab).append(";}");
            }

            p.append("throw ").append(evar.getRuntimeName()).append("}}");
        } else {
            p.append("catch(e){throw e;}");
        }
        return true;
    }

    @Override
    public boolean visit(Throw o) throws CompileException {
        p.append("throw ");
        o.getValue().accept(this);
        p.append("");
        return true;
    }

    @Override
    public boolean visit(This o) throws CompileException {
        ICodeGenerator icg = c.getGenerator(o.getType());
        if (icg != null && icg != this)
            return icg.operation(c, o, p);

        if (!c.getCurrentClass().isParent(o.getType())) {
            //Optional<VClass> cl = c.getCurrentClass().getDependencyParent();
                /*
                if (cl.isPresent() && cl.get() == o.getType()) {
                    System.out.println("getting this of parent " + c.getCurrentClass().hashCode() + " " + c.getCurrentClass());
                    return g.operation(c, TypeUtil.getParentThis(c.getCurrentClass()), p);
                }
                */
            throw new RuntimeException(new CompileException("Not support other this type. Current " + c.getCurrentClass() + ", this=" + o.getType(), o.getStartPoint())+"\n"+o.getStartPoint());
        }
        p.append("this");
        return true;
    }

    @Override
    public boolean visit(Switch o) throws CompileException {
        p.append("switch(");
        o.getValue().accept(this);
        p.append("){");
        for (Switch.Case ca : o.getCases()) {
            if (ca.value == null)
                p.append("default:");
            else {
                p.append("case ");
                ca.value.accept(this);
                p.append(":");
            }
            ca.block.accept(this);
        }
        p.append("}");
        return true;
    }

    @Override
    public boolean visit(StaticRef o) throws CompileException {
        ICodeGenerator icg = c.getGenerator(o.getType());
        if (icg != null && icg != this) {
            return icg.operation(c, o, p);
        }



        new ClassRecordRef(o.getType(), o.getStartPoint()).accept(this);
        p.append(".");
        p.append(CodeBuilder.scope(new ClassRecordRef(o.getType().getClassLoader().loadClass(ClassRecord.class.getName(), o.getStartPoint()), o.getStartPoint())).method("getPrototype").find().getRunTimeName());
        p.append("()");
        return true;
    }

    @Override
    public boolean visit(SetValue o) throws CompileException {
        if (o.getValue() instanceof SVar) {
            p.append("");
        } else p.append("");

        //p.add("", o.getPoint());
        o.getValue().accept(this);

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

        o.getNewValue().accept(this);

        return true;
    }

    @Override
    public boolean visit(SetField o) throws CompileException {
        VClass pp = o.getField().getParent();


        o.getScope().accept(this);
        p.append(".").append(o.getField().getRuntimeName())
                .append("=");
        o.getValue().accept(this);
        return true;
    }

    @Override
    public boolean visit(Parens o) throws CompileException {
        p.append("(");
        o.getValue().accept(this);
        p.append(")");
        return true;
    }

    @Override
    public boolean visit(NewClass o) throws CompileException {
        InvokeGenerator ig = c.getInvokeGenerator(o.constructor);
        if (ig != null) {
            Invoke inv = new Invoke(o.constructor, null);
            inv.arguments = o.arguments;
            return ig.generate(c, inv, p);
        }
        ICodeGenerator icg = c.getGenerator(o.constructor.getParent());
        if (icg != null && icg != this)
            return icg.operation(c, o, p);
        p.append("");
        new StaticRef(o.constructor.getParent(), o.getStartPoint()).accept(this);
        p.append(".").append("n").append(o.constructor.getRunTimeName()).append("(");
        boolean first = true;
        for (Value v : o.arguments) {
            if (!first)
                p.append(",");
            v.accept(this);
            first = false;
        }
        p.append(")");
        return true;
    }

    @Override
    public boolean visit(NewArrayLen o) throws CompileException {
        if (o.sizes.size() > 1) {
            throw new RuntimeException("Not supported multiarray! yet!");
        }
        new StaticRef(o.getType(), null).accept(this);
        p.append(".n").append(ArrayClass.CONSTRUCTOR).append("(");
        o.sizes.get(0).accept(this);
        p.append(")");
        return true;
    }

    @Override
    public boolean visit(NewArrayItems o) throws CompileException {
        VClass classArrayBuilder = o.getType().getClassLoader().loadClass(ArrayBuilder.class.getName(), o.getStartPoint());
        VMethod methodGet = MethodSelectorUtils.getMethodByName(classArrayBuilder, "create").get(0);

        return CodeBuilder.scopeStatic((classArrayBuilder)).invoke(methodGet, null, o.getStartPoint()).arg(
                new ClassRecordRef(o.getType(), null)
                ).arg(o).build().accept(this);
    }

    @Override
    public boolean visit(Label node) throws CompileException {
        throw new RuntimeException("Not supported yet");
    }

    @Override
    public boolean visit(InstanceOf o) throws CompileException {
        ICodeGenerator cg = c.getGenerator(o.getClazz());
        if (cg != null && cg != this) {
            return cg.operation(c, o, p);
        }

        VClass objectClass = o.getClazz().getClassLoader().loadClass(Object.class.getName(), o.getStartPoint());
        return CodeBuilder.scopeClass(o.getClazz()).method("isInstance").arg(objectClass).invoke(null, o.getStartPoint()).arg(o.getValue()).build().accept(this);
    }

    @Override
    public boolean visit(Increment o) throws CompileException {
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

        o.getValue().accept(this);

        switch (o.getIncType()) {
            case POST_DEC:
                p.append("--");
                break;
            case POST_INC:
                p.append("++");
                break;
        }
        return true;
    }

    @Override
    public boolean visit(GetValue o) throws CompileException {
        if (o.getValue() instanceof SVar) {
            p.append("");
        } else p.append("");

        o.getValue().accept(this);
        return true;
    }

    @Override
    public boolean visit(GetField o) throws CompileException {
        int pos = p.getCurrent();
        if (Modifier.isFinal(o.getField().getModificators()) && o.getField().init != null && (o.getField().init instanceof Const)) {
            return o.getField().init.accept(this);
        }
        o.getScope().accept(this);
        p.append(".");
        p.append(o.getField().getRuntimeName());
        //p.append();
        return true;
    }

    @Override
    public boolean visit(FunctionRef node) throws CompileException {
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public boolean visit(ForLoop o) throws CompileException {
        p.append("for(");
        o.init.accept(this);
        p.append(";");
        o.value.accept(this);
        p.append(";");
        o.update.accept(this);
        p.append(")");
        if (o.block != null) {
            o.block.accept(this);
        } else
            p.append("{}");
        return true;
    }

    @Override
    public boolean visit(DoWhileLoop o) throws CompileException {
        p.append("do");

        if (o.block == null)
            p.append("{}");
        else
            o.block.accept(this);

        p.append("while(");
        o.value.accept(this);
        p.append(")");
        return true;
    }

    @Override
    public boolean visit(Continue o) {
        p.append("continue");
        if (o.getLabel() != null) {
            p.append(" ").append(o.getLabel().getName());
        }
        return true;
    }

    @Override
    public boolean visit(Const o) throws CompileException {
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
    }

    @Override
    public boolean visit(Conditional o) throws CompileException {
        o.getValue().accept(this);
        p.append("?");
        o.getThenValue().accept(this);
        p.append(":");
        o.getElseValue().accept(this);
        return true;
    }

    @Override
    public boolean visit(ClassRef o) throws CompileException {
        new ClassRecordRef(o.refTo, o.getStartPoint()).accept(this);
        p.append(".");
        p.append(CodeBuilder.scope(new ClassRecordRef(o.refTo.getClassLoader().loadClass(ClassRecord.class.getName(), o.getStartPoint()), o.getStartPoint())).method("getAsClass").find().getRunTimeName());
        p.append("()");
        return true;
    }

    @Override
    public boolean visit(Break o) {
        p.append("break");
        if (o.getLabel() != null) {
            p.append(" ").append(o.getLabel().getName());
        }
        return true;
    }

    @Override
    public boolean visit(Assign o) throws CompileException {
        o.getVar().accept(this);
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
        o.getValue().accept(this);
        return true;
    }

    @Override
    public boolean visit(ArrayGet o) throws CompileException {
        VMethod getMethod = MethodSelectorUtils.getMethod(o.getValue().getType(), "get", null, o.getType().getClassLoader().loadClass("int", null));
        return CodeBuilder.scope(o.getValue()).invoke(getMethod, null, null).arg(o.getIndex()).build().accept(this);
    }

    @Override
    public boolean visit(ArrayAssign o) throws CompileException {
        VMethod getMethod = MethodSelectorUtils.getMethod(o.getVar().getType(), "set", null,
                o.getType().getClassLoader().loadClass("int", null),
                o.getType());
        Invoke inv = new Invoke(getMethod, o.getVar());
        inv.arguments.add(o.getIndexs());
        inv.arguments.add(o.getValue());
        inv.accept(this);
        return true;
    }

    protected GenerationContext c;
    protected Outbuffer p;

    @Override
    public boolean visit(VIf o) throws CompileException {
        p.append("if (");
        o.value.accept(this);
        p.append(")");
        if (o.thenBlock != null)
            o.thenBlock.accept(this);
        else {
            p.append("{}");
        }
        if (o.elseBlock != null) {
            p.append("else");
            o.elseBlock.accept(this);
        }
        return true;
    }

    @Override
    public boolean visit(Invoke o) throws CompileException {
        InvokeGenerator icg = c.getInvokeGenerator(o.getMethod());
        if (icg != null) {
            return icg.generate(c, o, p);
        }



        ICodeGenerator icg2 = c.getGenerator(o.getMethod());
        if (icg2 != null && icg2 != this)
            return icg2.operation(c, o, p);

        Predicate<Boolean> printArg = f -> {
            try {
                boolean first = f;
                for (Value v : o.arguments) {
                    if (!first) {
                        p.append(",");
                    }
                    v.accept(this);
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

        o.getScope().accept(this);

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
    }

    @Override
    public boolean visit(VBlock o) throws CompileException {
        p.append("{");
        for (Operation op : o.getOperations()) {
            if (op == null)
                continue;
            op.accept(this);
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
    }
}
