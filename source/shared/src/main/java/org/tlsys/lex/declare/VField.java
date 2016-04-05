package org.tlsys.lex.declare;

import org.tlsys.lex.Collect;
import org.tlsys.lex.Context;
import org.tlsys.lex.VVar;
import org.tlsys.lex.Value;

import java.io.*;
import java.util.Optional;
import java.util.function.Predicate;

public class VField extends VVar implements Member, CodeDynLoad {

    private static final long serialVersionUID = -652517458481464235L;
    private final int modificators;
    public transient Value init;
    //private final VClass parent;

    public VField(String realName, VClass clazz, int modificators, VClass parent) {
        super(realName, clazz, parent);
        this.modificators = modificators;
        //this.parent = parent;
    }

    public VField(String realName, String alias, VClass clazz, int modificators, VClass parent) {
        super(realName, alias, clazz, parent);
        this.modificators = modificators;
        //this.parent = parent;
    }

    @Override
    public int getModificators() {
        return modificators;
    }

    @Override
    public boolean isThis(String name) {
        return name.equals(getRealName()) || name.equals(getAliasName());
    }

    @Override
    public VClass getParent() {
        return (VClass) getParentContext();
    }

    @Override
    public void getUsing(Collect c) {
        c.add(init, getParent(), getType());
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        if (init != null && searchIn.test(init)) {
            Optional<Context> o = init.find(name, searchIn);
            if (o.isPresent())
                return o;
        }
        return super.find(name, searchIn);
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
            init = (Value) o;
        } else
            init = null;
    }

    Object writeReplace() throws ObjectStreamException {
        if (getParent().getClassLoader() != VClass.getCurrentClassLoader()) {
            return new FieldRef(realName, getParent());
        }
        return this;
    }

    private static class FieldRef implements Serializable {
        private static final long serialVersionUID = -1967975223687035241L;
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
