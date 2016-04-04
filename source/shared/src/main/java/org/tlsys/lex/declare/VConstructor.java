package org.tlsys.lex.declare;

import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.Collect;
import org.tlsys.lex.Invoke;
import org.tlsys.sourcemap.SourcePoint;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;

public class VConstructor extends VExecute {
    private static final long serialVersionUID = 6381674695841109642L;

    public Invoke parentConstructorInvoke;

    public VConstructor(SourcePoint point, VClass parent) {
        super(point, parent);
        try {
            returnType = parent.getClassLoader().loadClass("void");
        } catch (VClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isThis(String name) {
        return false;
    }

    public VConstructor() {
    }

    @Override
    public void getUsing(Collect c) {
        super.getUsing(c);
        if (parentConstructorInvoke != null)
            c.add(parentConstructorInvoke);
    }

    @Override
    public String getDescription() {
        return "<init>(" + getArgumentDescription() + ")";
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        super.visit(replaceControl);
        if (parentConstructorInvoke != null)
            ReplaceHelper.replace(parentConstructorInvoke, replaceControl).ifPresent(e->parentConstructorInvoke = e);
    }

    Object writeReplace() throws ObjectStreamException {
        if (getParent().getClassLoader() != VClass.getCurrentClassLoader()) {
            ArrayList<VClass> args = new ArrayList<>(arguments.size());
            for (int i = 0; i < arguments.size(); i++)
                args.add(arguments.get(i).getType());
            return new MethodRef(getParent(), args);
        }
        return this;
    }

    private static class MethodRef implements Serializable {
        private VClass parent;
        private ArrayList<VClass> arguments;


        public MethodRef(VClass parent, ArrayList<VClass> arguments) {
            this.parent = parent;
            this.arguments = arguments;
        }

        public VClass getParent() {
            return parent;
        }

        public ArrayList<VClass> getArguments() {
            return arguments;
        }

        Object readResolve() throws Exception {
            if (getParent().constructors == null)
                getParent().constructors = new ArrayList<>();
            return getParent().getConstructor(getArguments());
        }
    }
}
