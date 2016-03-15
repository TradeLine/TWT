package org.tlsys.lex.declare;

import org.tlsys.lex.Collect;
import org.tlsys.lex.Context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class VExecute implements Context, Member, CodeDynLoad {
    private static final long serialVersionUID = 3706412942276907442L;
    public VClass returnType;
    private String name;
    public String alias;
    public boolean force;

    public String getRunTimeName() {
        return name;
    }

    public void setRuntimeName(String name) {
        this.name = name;
    }

    public ArrayList<VArgument> arguments = new ArrayList<>();
    private VClass parent;
    public transient VBlock block = null;
    public String generator = null;
    public String invokeGenerator = null;

    public VExecute() {
    }

    public VExecute(VClass parent) {
        this.parent = parent;
    }

    public VClass getParent() {
        return parent;
    }

    private int modificators;

    public void setModificators(int modificators) {
        this.modificators = modificators;
    }

    @Override
    public int getModificators() {
        return modificators;
    }

    @Override
    public void getUsing(Collect c) {
        c.add(returnType);
        for (VArgument a : arguments)
            c.add(a);
        if (block != null)
            c.add(block);
    }
    public abstract String getDescription();

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        for (VArgument a : arguments) {
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
        for (VArgument a : arguments) {
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
}
