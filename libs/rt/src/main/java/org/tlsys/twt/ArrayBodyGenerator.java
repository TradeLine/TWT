package org.tlsys.twt;

import org.tlsys.Outbuffer;
import org.tlsys.lex.Assign;
import org.tlsys.lex.Invoke;
import org.tlsys.lex.SetField;
import org.tlsys.lex.This;
import org.tlsys.lex.declare.ArrayClass;
import org.tlsys.lex.declare.VExecute;

public class ArrayBodyGenerator extends DefaultGenerator {
    @Override
    public void generateExecute(GenerationContext ctx, VExecute execute, Outbuffer ps, CompileModuls moduls) throws CompileException {
        ICodeGenerator defaultCG = ctx.getGenerator(execute.getParent());
        ArrayClass ac = (ArrayClass) execute.getParent();
        if (execute == ac.constructor) {
            defaultCG.operation(ctx, new Invoke(execute.getParent().extendsClass.getConstructor(execute.getStartPoint()), new This(execute.getParent())), ps);
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
