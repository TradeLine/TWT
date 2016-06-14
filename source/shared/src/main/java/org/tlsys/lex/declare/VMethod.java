package org.tlsys.lex.declare;

import org.tlsys.MethodSelectorUtils;
import org.tlsys.lex.Collect;
import org.tlsys.sourcemap.SourcePoint;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VMethod extends VExecute {
    private static final long serialVersionUID = 7352639283063310734L;
    private final String realName;
    public VMethod brigTo;
    private transient VMethod replace;

    private transient HashSet<VMethod> replaced;

    public VMethod(SourcePoint point, String realName, VClass parent, VMethod brigTo) {
        super(point, parent);
        this.realName = realName;
        this.brigTo = brigTo;
    }

    public Set<VMethod> getReplaced() {
        if (replaced == null)
            return Collections.EMPTY_SET;
        return replaced;
    }

    private void addReplaced(VMethod method) {
        if (replaced == null)
            replaced = new HashSet<>();

        replaced.add(method);
    }

    private void removeReplaced(VMethod method) {
        if (replaced == null)
            return;
        replaced.remove(method);
    }

    @Override
    public void setRuntimeName(String name) {
        if (replace != null)
            throw new IllegalStateException("Can't set runtime name, becouse this method have replace method");
        super.setRuntimeName(name);
    }

    @Override
    public void getUsing(Collect c) {
        super.getUsing(c);
        if (replaced != null)
            for (VMethod m : replaced)
                c.add(m);
        if (replace != null)
            c.add(replace);
    }

    public VMethod getReplace() {
        return replace;
    }

    public void setReplace(VMethod method) {
        if (method == this)
            throw new IllegalArgumentException("Can't replace self");
        if (replace != null)
            replace.removeReplaced(this);
        replace = method;

        if (replace != null)
            replace.addReplaced(this);
    }

    @Override
    public String getDescription() {
        return (alias != null ? alias : getRunTimeName()) + "(" + getArgumentDescription() + ")";
    }

    @Override
    public boolean isThis(String name) {
        return this.getRunTimeName().equals(name) || name.equals(alias);
    }

    public String getRealName() {
        return realName;
    }

    @Override
    public String getRunTimeName() {
        if (replace != null)
            return replace.getRunTimeName();
        return super.getRunTimeName();
    }

    private void readObject(ObjectInputStream in) throws Exception {
        in.defaultReadObject();
        replace = (VMethod) in.readObject();

        if (replace != null)
            replace.addReplaced(this);
    }

    private void writeObject(ObjectOutputStream out) throws Exception {
        out.defaultWriteObject();
        out.writeObject(replace);
    }

    Object writeReplace() throws ObjectStreamException {
        if (getParent().getClassLoader() != VClass.getCurrentClassLoader()) {
            ArrayList<VClass> args = new ArrayList<>(arguments.size());
            for (int i = 0; i < arguments.size(); i++)
                args.add(arguments.get(i).getType());
            return new MethodRef(getRunTimeName(), getParent(), args);
        }
        return this;
    }

    private static class MethodRef implements Serializable {
        private static final long serialVersionUID = -1899868535852784733L;
        private String alias;
        private VClass parent;
        private ArrayList<VClass> arguments;

        public MethodRef(String alias, VClass parent, ArrayList<VClass> arguments) {
            this.alias = alias;
            this.parent = parent;
            this.arguments = arguments;
        }

        public String getAlias() {
            return alias;
        }

        public VClass getParent() {
            return parent;
        }

        public ArrayList<VClass> getArguments() {
            return arguments;
        }

        Object readResolve() throws Exception {
            return MethodSelectorUtils.getMethod(getParent(), getAlias(), getArguments(), null);
        }
    }
}
