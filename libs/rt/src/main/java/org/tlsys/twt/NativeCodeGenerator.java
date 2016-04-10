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
            ps.append(clazz.fullName+".").append(f.getRuntimeName());
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
        /*
        if (meth.alias != null)
            ps.append(meth.alias);
        else
        */
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
        ps.add("{", meth.getBlock().getStartPoint());
        for (Operation o : meth.getBlock().getOperations()) {
            if (operation(ctx, o, ps)) ;
                ps.append(";");
        }
        ps.add("}", meth.getBlock().getEndPoint());
        generateMethodEnd(ctx, meth, ps);
    }

    @Override
    public boolean operation(GenerationContext ctx, Operation op, Outbuffer ps) throws CompileException {
        if (op instanceof Invoke) {
            Invoke inv = (Invoke) op;

            InvokeGenerator icg = ctx.getInvokeGenerator(((Invoke) op).getMethod());
            if (icg != null && icg != this)
                return icg.generate(ctx, inv, ps);

            ICodeGenerator icg2 = ctx.getGenerator(inv.getMethod());
            if (icg2 != null && icg2 != this)
                return icg2.operation(ctx, inv, ps);

            if (inv.getMethod() instanceof VConstructor) {//если происходит вызов конструктра, то игнорируем!
                ps.append("/*IGNORED!*/");
                return false;
            }

            /*
            if(inv.getScope() instanceof Lambda) {
                operation(ctx, inv.getScope(), ps);
                ps.append(".call(this");
                for (Value v : inv.arguments) {
                    ps.append(",");
                    operation(ctx, v, ps);
                }
                ps.append(")");
                return true;
            }
            */
        }

        if (op instanceof StaticRef) {
            StaticRef sr = (StaticRef) op;
            ICodeGenerator icg = ctx.getGenerator(sr.getType());
            if (icg != this)
                return icg.operation(ctx, op, ps);
            ps.add(sr.getType().fullName, sr.getPoint(), sr.getType().getRealName());
            ps.append("/*FROM NATIVE 222*/");
            return true;
        }

        if (op instanceof ClassRef) {
            ClassRef sr = (ClassRef) op;
            ICodeGenerator icg = ctx.getGenerator(sr.refTo);
            if (icg != this)
                return icg.operation(ctx, op, ps);
            ps.add(sr.refTo.fullName, sr.getPoint(), sr.refTo.getRealName());
            ps.append("/*FROM NATIVE 111*/");
            return true;
        }
/*
        if (op instanceof NewClass) {
            NewClass nc = (NewClass) op;
            //if (nc.constructor.getParent()==ctx.getCurrentClass()) {
            ps.append(nc.constructor.getParent().fullName).append(".n").append(nc.constructor.name).append("(");
            boolean first = true;
            for (Value v : nc.arguments) {
                if (!first)
                    ps.append(",");
                operation(ctx, v, ps);
                first = false;
            }
            ps.append(")");
            //}
            return true;
        }
        */

        if (op instanceof Lambda) {
            Lambda l = (Lambda)op;
            ps.append("{").append(l.getMethod().getRunTimeName()).append(":");
            ps.append("function(");
            boolean first = true;
            for(VArgument a : l.getMethod().getArguments()) {
                if(!first)
                    ps.append(",");
                ps.add(a.getRuntimeName(), a.getPoint(), a.getRealName());
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

        /*
        if (op instanceof DeclareClass) {
            DeclareClass dc = (DeclareClass)op;
            ICodeGenerator gen = ctx.getGenerator(dc.getType());
            if (gen == this)
                return false;

            return gen.member(ctx, dc.getType(), ps);
        }
        */

        return super.operation(ctx, op, ps);
    }
}
