package org.tlsys.lex;

import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VArgument;
import org.tlsys.lex.declare.VBlock;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VMethod;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class Lambda extends Value implements Context {
    private static final long serialVersionUID = 3557470342170348577L;
    private VBlock block;
    public ArrayList<VArgument> arguments = new ArrayList<>();

    private VMethod method;
    private Context parentContext;

    public VMethod getMethod() {
        return method;
    }

    public VBlock getBlock() {
        return block;
    }

    public Lambda() {
        //throw new RuntimeException("Lambda creation!");
    }

    public Context getParentContext() {
        return parentContext;
    }

    public void setBlock(VBlock block) {
        this.block = block;
    }

    public Lambda(VMethod method, Context parentContext) {
        this.method = method;
        this.parentContext = parentContext;
        //throw new RuntimeException("Lambda creation!");
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        for (VArgument a : arguments)
            if (name.equals(a.getRealName()) || name.equals(a.getAliasName()))
                return Optional.of(a);
        if (!searchIn.test(parentContext))
            return Optional.empty();
        return parentContext.find(name, searchIn.and(e->e!=this));
    }

    @Override
    public VClass getType() {
        return method.getParent();
    }

    @Override
    public void getUsing(Collect c) {
        c.add(arguments.stream().toArray(VArgument[]::new)).add(block);
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        super.visit(replaceControl);
        ReplaceHelper.replace(block, replaceControl).ifPresent(e->block = e);
    }
}
