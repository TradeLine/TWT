package org.tlsys.twt;

import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.annotations.InvokeGen;
import org.tlsys.twt.rt.java.lang.TClassLoader;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

public class DefaultGenerator implements ICodeGenerator {


    private static final HashSet<VClass> generatedClasses = new HashSet<>();

    protected void addGenerated(VClass clazz) {
        generatedClasses.add(clazz);
    }

    protected boolean isGenerated(VClass clazz) {
        return generatedClasses.contains(clazz);
    }

    private interface Gen<T> {
        public boolean gen(GenerationContext ctx, T op, PrintStream ps, ICodeGenerator gen) throws CompileException;
    }

    public static <T> void addGen(Class<T> clazz, Gen<T> gen) {
        generators.put(clazz, gen);
    }

    private static HashMap<Class, Gen> generators = new HashMap<>();

    private static Value getClassViaTypeProvider(VClass vClass) throws VClassNotFoundException {
        VClass typeProviderClass = vClass.getClassLoader().loadClass(TClassLoader.TypeProvider.class.getName());
        VBlock body = new VBlock();
        body.operations.add(new Return(new StaticRef(vClass)));
        Lambda lambda = new Lambda(body, typeProviderClass.methods.get(0), null);
        return lambda;
    }

    /*
    @Override
    public boolean member(GenerationContext ctx, Member op, PrintStream ps) throws CompileException {
        throw new RuntimeException("Not supported yet " + op.getClass().getName());
    }
    */

    static {
        addGen(Const.class, (c,o,p,g)->{
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

        addGen(Return.class, (c,o,p,g)->{
            p.append("return");
            if (o.getValue() != null) {
                p.append(" ");
                g.operation(c, o.getValue(), p);
            }
            return true;
        });

        addGen(This.class, (c,o,p,g)->{
            if (o.getType() != c.getCurrentClass())
                throw new RuntimeException("Not support other this type");
            p.append("this");
            return true;
        });

        addGen(Invoke.class, (c,o,p,g)->{
            InvokeGenerator icg = c.getInvokeGenerator(o.getMethod());
            if (icg != null && icg != c)
                return icg.generate(c,o,p);

            g.operation(c,o.getSelf(),p);
            p.append(".");
            if (o.getMethod() instanceof VMethod) {
                VMethod m = (VMethod)o.getMethod();
                p.append(m.alias);
            } else
                throw new RuntimeException("Invoke not supported");
            p.append("(");
            boolean first = true;
            for (Value v : o.arguments) {
                if (!first)
                    p.append(",");
                g.operation(c, v, p);
                first = false;
            }
            p.append(")");
            return true;
        });

        addGen(VArgument.class, (c,o,p,g)->{
            p.append(o.name);
            return true;
        });
        addGen(DeclareVar.class, (c,o,p,g)->{
            p.append("var ").append(o.getVar().name);
            if (o.init != null) {
                p.append("=");
                g.operation(c, o.init, p);
            }
            return true;
        });
        addGen(GetField.class, (c,o,p,g)->{
            g.operation(c,o.getScope(),p);
            p.append(".");
            p.append(o.getField().name);
            return true;
        });

        addGen(StaticRef.class, (c,o,p,g)->{
            ICodeGenerator icg = c.getGenerator(o.getType());
            if (icg != null && icg != g)
                return icg.operation(c,o,p);
            throw new RuntimeException("Class ref not supported yet");
        });

        addGen(NewClass.class, (c,o,p,g)->{
            InvokeGenerator ig = c.getInvokeGenerator(o.constructor);
            if (ig != null) {
                Invoke inv = new Invoke(o.constructor, null);
                inv.arguments = o.arguments;
                return ig.generate(c, inv, p);
            }
            ICodeGenerator icg = c.getGenerator(o.constructor.getParent());
            if (icg != null && icg != c)
                return icg.operation(c,o,p);
            //p.append(".");
            //p.append(o.getField().name);
            throw new RuntimeException("new operator not suppported");
        });

        addGen(DeclareClass.class, (c,o,p,g)->{
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

        addGen(SVar.class, (c,o,p,g)->{
            p.append(o.name);
            return true;
        });

        addGen(SetField.class, (c,o,p,g)->{
            g.operation(c, o.getScope(), p);
            p.append(".").append(o.getField().alias).append("=");
            g.operation(c, o.getValue(), p);
            return true;
        });

        addGen(VIf.class, (c,o,p,g)->{
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
    }

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
}
