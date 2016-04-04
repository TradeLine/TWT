package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.sourcemap.SourcePoint;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;

public class VMethod extends VExecute {
    private static final long serialVersionUID = 7352639283063310734L;
    public VMethod brigTo;
    private VMethod replace;

    public void setReplace(VMethod method) {
        if (method == this)
            throw new IllegalArgumentException("Can't replace self");
        replace = method;
    }

    @Override
    public void setRuntimeName(String name) {
        if (replace != null)
            throw new IllegalStateException("Can't set runtime name, becouse this method have replace method");
        super.setRuntimeName(name);
    }

    public VMethod getReplace() {
        return replace;
    }

    private final String realName;

    public VMethod(SourcePoint point, String realName, VClass parent, VMethod brigTo) {
        super(point, parent);
        this.realName = realName;
        this.brigTo = brigTo;
    }

    @Override
    public String getDescription() {
        return (alias!=null?alias:getRunTimeName()) + "(" + getArgumentDescription() + ")";
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
            return getParent().getMethod(getAlias(), getArguments());
        }
    }
}
