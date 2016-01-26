package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.Collect;
import org.tlsys.lex.Context;
import org.tlsys.lex.SVar;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class VExecute implements Context, Member, CodeDynLoad {
    private static final long serialVersionUID = 3706412942276907442L;
    public VClass returnType;
    public String name;
    public String alias;

    public ArrayList<VArgument> arguments = new ArrayList<>();
    private VClass parent;
    private transient Symbol symbol;
    public transient VBlock block = null;
    public String generator = null;
    public String invokeGenerator = null;

    public VExecute() {
    }

    public VExecute(VClass parent, Symbol symbol) {
        this.parent = parent;
        this.symbol = symbol;
    }

    @Override
    public Symbol getSymbol() {
        return symbol;
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
        //c.add(getParent());
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        for (VArgument a : arguments)
            if (a.getSymbol() == symbol)
                return Optional.of(a);
        if (searchIn.test(getParent()))
            return getParent().find(symbol, searchIn);
        return Optional.empty();
    }

    @Override
    public void loadCode(ObjectInputStream input) throws IOException, ClassNotFoundException {
        if (input.readBoolean())
            block = (VBlock) input.readObject();
        else
            block = null;
    }

    @Override
    public void saveCode(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeBoolean(block != null);
        if (block != null)
            outputStream.writeObject(block);
    }
}
