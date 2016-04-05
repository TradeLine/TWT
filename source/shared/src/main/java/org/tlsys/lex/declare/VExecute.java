package org.tlsys.lex.declare;

import org.tlsys.ArgumentModificator;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.Collect;
import org.tlsys.lex.Context;
import org.tlsys.sourcemap.SourcePoint;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class VExecute implements Context, Member, CodeDynLoad {
    private static final long serialVersionUID = 3706412942276907442L;
    private final ArrayList<ArgumentModificator> mods = new ArrayList<>();
    public VClass returnType;
    public String alias;
    public boolean force;
    public String bodyGenerator;
    public String generator = null;
    public String invokeGenerator = null;
    protected ArrayList<VArgument> arguments = new ArrayList<>();
    private String name;
    private SourcePoint point;
    private VClass parent;
    private transient VBlock block = null;
    private int modificators;

    public VExecute() {
    }

    public VExecute(SourcePoint point, VClass parent) {
        this.parent = parent;
    }

    public ArrayList<ArgumentModificator> getMods() {
        return mods;
    }

    public String getRunTimeName() {
        return name;
    }

    public void setRuntimeName(String name) {
        this.name = name;
    }

    public List<VArgument> getArguments() {
        if (mods.isEmpty())
            return arguments;
        List<VArgument> args = new ArrayList<>(arguments.size());
        for (ArgumentModificator am : mods)
            args = am.getArguments(args);
        args.addAll(arguments);
        return args;
    }

    public VBlock getBlock() {
        return block;
    }

    public void setBlock(VBlock block) {
        if (this.block == block)
            return;

        for (ArgumentModificator ar : mods) {
            ar.setBody(this.block, block);
        }

        this.block = block;
    }

    public SourcePoint getPoint() {
        return point;
    }

    public VClass getParent() {
        return parent;
    }

    @Override
    public int getModificators() {
        return modificators;
    }

    public void setModificators(int modificators) {
        this.modificators = modificators;
    }

    @Override
    public void getUsing(Collect c) {
        c.add(returnType);
        for (VArgument a : getArguments())
            c.add(a);
        if (block != null)
            c.add(block);
    }
    public abstract String getDescription();

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        for (VArgument a : getArguments()) {
            if (name.equals(a.getAliasName()) || name.equals(a.getRealName()))
                return Optional.of(a);
        }
        if (searchIn.test(getParent()))
            return getParent().find(name, searchIn);
        return Optional.empty();
    }

    @Override
    public void loadCode(ObjectInputStream input) throws IOException, ClassNotFoundException {
        if (input.readBoolean())
            block = (VBlock) input.readObject();
        else
            block = null;
    }

    public String getArgumentDescription() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (VArgument a : getArguments()) {
            if (!first)
                sb.append("; ");
            sb.append(a.getType().getRealName());
            first = false;
        }
        return sb.toString();
    }

    @Override
    public void saveCode(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeBoolean(block != null);
        if (block != null)
            outputStream.writeObject(block);
    }

    @Override
    public String toString() {
        return getParent().getRealName()+"::"+getDescription();
    }

    public void visit(ReplaceVisiter replaceControl) {
        if (block != null)
        block.visit(replaceControl);
    }

    public void addArg(VArgument l) {
        arguments.add(l);
    }
}
