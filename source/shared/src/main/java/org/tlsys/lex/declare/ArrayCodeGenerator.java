package org.tlsys.lex.declare;

import org.tlsys.Outbuffer;
import org.tlsys.lex.*;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.CompileModuls;
import org.tlsys.twt.GenerationContext;
import org.tlsys.twt.ICodeGenerator;

import java.io.PrintStream;

public class ArrayCodeGenerator implements ICodeGenerator {
    @Override
    public void generateClass(GenerationContext context, CompileModuls.ClassRecord record, Outbuffer ps) throws CompileException {
        throw new RuntimeException("Not supported");
    }

    @Override
    public boolean operation(GenerationContext context, Operation operation, Outbuffer out) throws CompileException {
        throw new RuntimeException("Not supported");
    }

    @Override
    public void generateExecute(GenerationContext ctx, VExecute execute, Outbuffer ps, CompileModuls moduls) throws CompileException {
        ICodeGenerator defaultCG = ctx.getGenerator(execute.getParent());
        ArrayClass ac = (ArrayClass)execute.getParent();
        if (execute == ac.constructor) {
            defaultCG.operation(ctx, new Invoke(execute.getParent().extendsClass.getConstructor(), new This(execute.getParent())), ps);
            ps.append(";");

            ps.append("this.").append(ac.jsArray.getRuntimeName()).append("=new Array(").append(execute.getArguments().get(0).getRuntimeName()).append(");");
            defaultCG.operation(ctx, new SetField(new This(ac), ac.lengthField, execute.getArguments().get(0), Assign.AsType.ASSIGN, null, null), ps);
            ps.append(";");
            return;
        }

        if (execute == ac.get) {
            ps.append("return this.").append(ac.jsArray.getRuntimeName()).append("[").append(execute.getArguments().get(0).getRuntimeName()).append("];");
            return;
        }

        if (execute == ac.set) {
            ps.append("this.").append(ac.jsArray.getRuntimeName()).append("[").append(execute.getArguments().get(0).getRuntimeName()).append("]=").append(execute.getArguments().get(1).getRuntimeName()).append(";");
            return;
        }

        throw new RuntimeException("Not supported method " + execute);
    }
}
