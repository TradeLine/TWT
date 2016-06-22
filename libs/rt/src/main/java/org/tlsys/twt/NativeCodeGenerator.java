package org.tlsys.twt;

import org.tlsys.Outbuffer;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;

public class NativeCodeGenerator extends DefaultGenerator implements ICodeGenerator {
    //private static final String CLASS_NAME = "cl";


    @Override
    public void generateClass(GenerationContext ctx, CompileModuls.ClassRecord record, Outbuffer ps) throws CompileException {
        VClass clazz = record.getClazz();
        ps.append("var ").append(clazz.fullName).append("=function(){");

        for (VField f : clazz.getLocalFields()) {
            if (f.isStatic())
                continue;
            ps.append("this.").append(f.getRuntimeName());
            if (f.init != null) {
                ps.append("=");
                operation(ctx, f.init, ps);
            }
            ps.append(";");
        }

        ps.append("};");


        for (VField f : clazz.getLocalFields()) {
            if (!f.isStatic())
                continue;
            ps.append(clazz.fullName + ".").append(f.getRuntimeName());
            ps.append("=");
            if (f.init != null) {

                operation(ctx, f.init, ps);
            } else ps.append("null");
            ps.append(";");
        }



        /*
        if (constructor.block != null)
            for (Operation o : constructor.block.operations) {
                if (operation(ctx, o, ps))
                    ps.append(";");
            }

        */

        for (VExecute m : record.getExe()) {
            ctx.getGenerator(m).generateExecute(ctx, m, ps, null);
            if (m instanceof VConstructor)
                ps.append(m.getParent().fullName).append(".n").append(m.getRunTimeName()).append("=function(){var o=new ").append(m.getParent().fullName).append("();o.").append(m.getRunTimeName()).append(".apply(o,arguments);return o;};");
        }
    }

    @Override
    protected void generateMethodStart(GenerationContext ctx, VExecute meth, Outbuffer ps) {
        ps.append(meth.getParent().fullName).append(".");
        if (!meth.isStatic())
            ps.append("prototype.");
        /*
        if (meth.alias != null)
            ps.append(meth.alias);
        else
        */
        ps.append(meth.getRunTimeName());
        ps.append("=function(");
        boolean first = true;
        for (VArgument ar : meth.getArguments()) {
            if (!first)
                ps.append(",");
            ps.append(ar.getRuntimeName());
            first = false;
        }
        ps.append(")");
    }

    @Override
    protected void generateMethodEnd(GenerationContext ctx, VExecute execute, Outbuffer ps) {
        ps.append(";");
    }

    @Override
    protected void generateMethodNull(GenerationContext ctx, VExecute meth, Outbuffer ps) {
        ps.append(meth.getParent().fullName).append(".");
        if (!meth.isStatic())
            ps.append("prototype.");
        ps.append(meth.getRunTimeName());
        ps.append("=null;");
    }

    @Override
    public void generateExecute(GenerationContext ctx, VExecute meth, Outbuffer ps, CompileModuls moduls) throws CompileException {
        if (meth.getBlock() == null) {
            generateMethodNull(ctx, meth, ps);
            return;
        }
        generateMethodStart(ctx, meth, ps);
        ps.append("{");
        for (Operation o : meth.getBlock().getOperations()) {
            if (operation(ctx, o, ps)) ;
            ps.append(";");
        }
        ps.append("}");
        generateMethodEnd(ctx, meth, ps);
    }

    private boolean generateLambda(Lambda l, Outbuffer ps, GenerationContext ctx) throws CompileException {
        ps.append("{").append(l.getMethod().getRunTimeName()).append(":");
        ps.append("function(");
        boolean first = true;
        for (VArgument a : l.getMethod().getArguments()) {
            if (!first)
                ps.append(",");
            ps.append(a.getRuntimeName());
            first = false;
        }
        ps.append(")");
        if (l.getBlock() == null) {
            ps.append("{}");
        } else
            operation(ctx, l.getBlock(), ps);
        ps.append("}");
        return true;
    }

    @Override
    public boolean visit(Value node) throws CompileException {
        if (node instanceof Lambda) {
            Lambda l = (Lambda) node;
            return generateLambda(l, p, c);
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(ClassRef sr) throws CompileException {
        ICodeGenerator icg = c.getGenerator(sr.refTo);
        if (icg != this)
            return icg.operation(c, sr, p);
        p.append(sr.refTo.fullName);
        p.append("/*FROM NATIVE 111*/");
        return true;
    }

    @Override
    public boolean visit(StaticRef sr) throws CompileException {
        ICodeGenerator icg = c.getGenerator(sr.getType());
        if (icg != this)
            return icg.operation(c, sr, p);
        p.append(sr.getType().fullName);
        p.append("/*FROM NATIVE 222*/");
        return true;
    }

    @Override
    public boolean visit(Invoke inv) throws CompileException {

        InvokeGenerator icg = c.getInvokeGenerator(inv.getMethod());
        if (icg != null && icg != this)
            return icg.generate(c, inv, p);

        ICodeGenerator icg2 = c.getGenerator(inv.getMethod());
        if (icg2 != null && icg2 != this)
            return icg2.operation(c, inv, p);

        if (inv.getMethod() instanceof VConstructor) {//если происходит вызов конструктра, то игнорируем!
            p.append("/*IGNORED!*/");
            return false;
        }

        return super.visit(inv);
    }
}
