package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class VField extends VVar implements Member, CodeDynLoad {

    private static final long serialVersionUID = -652517458481464235L;
    public transient Operation init;
    public String alias;
    private int modificators;
    private transient Symbol.VarSymbol symbol;
    private VClass parent;

    public VField(VClass clazz, int modificators, Symbol.VarSymbol symbol, VClass parent) {
        super(clazz, symbol);
        this.modificators = modificators;
        this.symbol = symbol;
        this.parent = parent;
    }

    public VField() {
    }

    @Override
    public int getModificators() {
        return modificators;
    }

    @Override
    public boolean isThis(String name) {
        return this.name.equals(name) || name.equals(alias);
    }

    @Override
    public VClass getParent() {
        return parent;
    }

    @Override
    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public void getUsing(Collect c) {
        c.add(init, parent, getType());
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        if (init != null && searchIn.test(init)) {
            Optional<SVar> o = init.find(symbol, searchIn);
            if (o.isPresent())
                return o;
        }
        return super.find(symbol, searchIn);
    }

    @Override
    public void saveCode(ObjectOutputStream output) throws IOException {
        output.writeBoolean(init != null);
        if (init != null)
            output.writeObject(init);
    }

    @Override
    public void loadCode(ObjectInputStream input) throws IOException, ClassNotFoundException {
        if (input.readBoolean()) {
            Object o = input.readObject();
            init = (Operation) o;
        } else
            init = null;
    }

    Object writeReplace() throws ObjectStreamException {
        if (getParent().getClassLoader() != VClass.getCurrentClassLoader()) {
            return new FieldRef(name, getParent());
        }
        return this;
    }

    private static class FieldRef implements Serializable {
        private String name;
        private VClass parent;

        public FieldRef(String name, VClass parent) {
            this.name = name;
            this.parent = parent;
        }

        public VClass getParent() {
            return parent;
        }

        public String getName() {
            return name;
        }

        Object readResolve() throws Exception {
            return getParent().getField(getName());
        }
    }
}
