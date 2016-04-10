package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VConstructor;
import org.tlsys.sourcemap.SourcePoint;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class NewClass extends Value implements HavinSourceStart {
    private static final long serialVersionUID = -2655271103608417622L;
    public VConstructor constructor;
    public ArrayList<Value> arguments = new ArrayList<>();
    private SourcePoint point;

    public NewClass() {
    }

    public NewClass(VConstructor constructor, SourcePoint sourcePoint) {
        this.constructor = constructor;
        this.point = sourcePoint;
    }

    @Override
    public SourcePoint getPoint() {
        return point;
    }

    public NewClass addArg(Value value) {
        arguments.add(value);
        return this;
    }

    @Override
    public VClass getType() {
        return constructor.getParent();
    }

    @Override
    public void getUsing(Collect c) {
        c.add(constructor);
        for (Value v : arguments)
            c.add(v);
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        super.visit(replaceControl);
        for (int i = 0; i < arguments.size(); i++) {
            Optional<Value> v = ReplaceHelper.replace(arguments.get(i), replaceControl);
            if (v.isPresent())
                arguments.set(i, v.get());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[NEW ").append(constructor.getParent().getRealName()).append(" :: ").append(constructor.getDescription());
        sb.append("(");
        boolean first = true;
        for (Value v : arguments) {
            if (!first)
                sb.append(", ");
            sb.append(v);
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
}
