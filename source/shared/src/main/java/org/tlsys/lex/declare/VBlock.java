package org.tlsys.lex.declare;

import org.tlsys.BlockModificator;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class VBlock extends Operation implements Using, CanUse, Context {
    private static final long serialVersionUID = 7031713493204208024L;
    private final ArrayList<Operation> operations = new ArrayList<>();

    private transient Context parentContext;
    private ArrayList<BlockModificator> mods = new ArrayList<>();

    public void addMod(BlockModificator blockModificator) {
        if (mods.add(blockModificator))
            blockModificator.onAdd(this);
    }

    public void removeMod(BlockModificator blockModificator) {
        if (mods.remove(blockModificator))
            blockModificator.onRemove(this);
    }

    public VBlock(Context parentContext) {
        this.parentContext = parentContext;
    }

    public VBlock() {
    }

    public List<Operation> getOperations() {
        if (mods.isEmpty())
            return operations;

        List<Operation> l = new ArrayList<>(operations);

        for (BlockModificator bm : mods) {
            l = bm.getOperations(l);
        }


        return l;
    }

    public List<Operation> getNativeOperations() {
        return operations;
    }

    public VBlock add(Operation operation) {
        operations.add(operation);
        return this;
    }

    @Override
    public void getUsing(Collect c) {
        for (Operation o : operations)
            c.add(o);
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        for (Operation o : operations) {
            if (!searchIn.test(o))
                continue;
            Optional<Context> v = o.find(name, searchIn.and(e -> e != this));
            if (v.isPresent())
                return v;
        }
        if (searchIn.test(parentContext))
            return parentContext.find(name, searchIn.and(e -> e != this));
        return Optional.empty();
    }

    public Context getParentContext() {
        return parentContext;
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        for (int i = 0; i < operations.size(); i++) {
            Optional<Operation> op = ReplaceHelper.replace(operations.get(i), replaceControl);
            if (op.isPresent())
                operations.set(i, op.get());
        }
    }
}
