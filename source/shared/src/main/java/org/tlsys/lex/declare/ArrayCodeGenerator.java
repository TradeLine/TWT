package org.tlsys.lex.declare;

public class ArrayCodeGenerator /* implements MethodBodyBuilder*/ {
    /*
    @Override
    public void generateClass(GenerationContext context, CompileModuls.ClassRecord record, Outbuffer ps) throws CompileException {
        throw new RuntimeException("Not supported");
    }

    @Override
    public boolean operation(GenerationContext context, Operation operation, Outbuffer out) throws CompileException {
        return context.getGenerator(context.getCurrentClass()).operation(context, operation, out);
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

    @Override
    public VBlock buildMethodBody(VExecute execute) {
        ArrayClass ac = (ArrayClass)execute.getParent();

        VBlock b = new VBlock(execute, null, null);

        if (execute == ac.constructor) {
            org.tlsys.CodeBuilder.scope(ac.extendsClass).constructor().invoke().build()
        }

        if (execute == ac.get) {

        }

        if (execute == ac.set) {

        }

        throw new RuntimeException("Not supported method " + execute);
    }
    */
}
