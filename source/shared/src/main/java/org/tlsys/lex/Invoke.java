package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VExecute;
import org.tlsys.sourcemap.SourcePoint;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class Invoke extends Value implements HavinSourceStart, HavingScope {
    private static final long serialVersionUID = -2146704597568339297L;
    private VExecute method;
    public ArrayList<Value> arguments = new ArrayList<>();
    private Value self;
    public VClass returnType;
    private SourcePoint point;

    public Invoke() {
    }

    public Invoke(VExecute method, Value self) {
        this(method, self, null);
    }

    public Invoke(VExecute method, Value self, SourcePoint point) {
        this.self = self;
        this.method = Objects.requireNonNull(method);
        this.point = point;
    }

    @Override
    public SourcePoint getPoint() {
        return point;
    }

    public Invoke addArg(Value value) {
        arguments.add(value);
        return this;
    }

    @Override
    public Value getScope() {
        return self;
    }

    public VExecute getMethod() {
        return method;
    }

    @Override
    public void getUsing(Collect c) {
        c.add(self);
        c.add(method);
        for (Value v : arguments)
            c.add(v);
    }

    @Override
    public VClass getType() {
        return returnType;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        ReplaceHelper.replace(self, replaceControl).ifPresent(e->self = e);
        for (int i =0; i < arguments.size(); i++) {
            Optional<Operation> op = ReplaceHelper.replace(arguments.get(i), replaceControl);
            if (op.isPresent())
                arguments.set(i, (Value) op.get());
        }
    }
}
