package org.tlsys.twt.rt.java.lang;

import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.DefaultGenerator;
import org.tlsys.twt.GenerationContext;
import org.tlsys.twt.ICodeGenerator;

import java.io.PrintStream;

public class NativeCodeGenerator extends DefaultGenerator implements ICodeGenerator {
    //private static final String CLASS_NAME = "cl";

    @Override
    public boolean member(GenerationContext ctx, Member op, PrintStream ps) throws CompileException {
        if (op instanceof VClass) {
            VClass clazz = (VClass) op;
            ps.append("function ").append(clazz.fullName).append("(");
            if (clazz.constructors.size() != 1)
                throw new IllegalArgumentException("Native Code Generator Not support mony constructors");

            VConstructor constructor = clazz.constructors.get(0);

            boolean first = true;
            for (VArgument ar : constructor.arguments) {
                if (!first)
                    ps.append(",");
                ps.append(ar.name);
                first = false;
            }

            ps.append("){\n");

            for (VField f : clazz.fields) {
                if (f.isStatic())
                    continue;
                ps.append("this.").append(f.name);
                if (f.init != null) {
                    ps.append("=");
                    operation(ctx, f.init, ps);
                }
                ps.append(";");
            }

            if (constructor.block != null)
                for (Operation o : constructor.block.operations) {
                    if (operation(ctx, o, ps))
                        ps.append(";\n");
                }
            ps.append("}\n");

            for (VMethod m : clazz.methods) {
                member(ctx, m, ps);
            }

            return true;
        }
        if (op instanceof VMethod) {
            VMethod meth = (VMethod) op;
            ps.append(meth.getParent().fullName).append(".");
            if (!meth.isStatic())
                ps.append("prototype.");
            if (meth.alias != null)
                ps.append(meth.alias);
            else
                ps.append(meth.name);
            if (meth.block == null) {
                ps.append("null;\n");
                return true;
            }
            ps.append("(");
            boolean first = true;
            for (VArgument ar : meth.arguments) {
                if (!first)
                    ps.append(",");
                ps.append(ar.name);
                first = false;
            }
            ps.append("){\n");
            for (Operation o : meth.block.operations) {
                if (operation(ctx, o, ps));
                    ps.append(";\n");
            }
            ps.append("};\n");
            return true;
        }

        throw new RuntimeException("Not supported yet " + op.getClass().getName());
    }

    @Override
    public boolean operation(GenerationContext ctx, Operation op, PrintStream ps) throws CompileException {
        if (op instanceof Invoke) {
            Invoke inv = (Invoke) op;
            if (inv.getMethod() instanceof VConstructor)//если происходит вызов родительского конструктра, то игнорируем!
                return false;
        }

        if (op instanceof NewClass) {
            NewClass nc = (NewClass)op;
            if (nc.constructor.getParent()==ctx.getCurrentClass()) {
                ps.append("new ").append(nc.constructor.getParent().fullName).append("(");
                boolean first = true;
                for (Value v : nc.arguments) {
                    if (!first)
                        ps.append(",");
                    operation(ctx, v, ps);
                    first = false;
                }
                ps.append(")");
            }
            return true;
        }

        if (op instanceof DeclareClass) {
            DeclareClass dc = (DeclareClass)op;
            ICodeGenerator gen = ctx.getGenerator(dc.getType());
            if (gen == this)
                return false;

            return gen.member(ctx, dc.getType(), ps);
            /*
            VClass classLoaderClass = ctx.getCurrentClass().getClassLoader().loadClass(ClassLoader.class.getName());
            VClass classBinClass = ctx.getCurrentClass().getClassLoader().loadClass(ClassBin.class.getName());

            VMethod addClassMethod = classLoaderClass.getMethod("addClass", classBinClass);
            Lambda creator = new Lambda();
            */
        }

        return super.operation(ctx, op, ps);
    }
}
