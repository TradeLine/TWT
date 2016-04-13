package org.tlsys.lex;

import org.tlsys.HavinSourceEnd;
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

public class Invoke extends Value implements HavinSourceStart, HavinSourceEnd, HavingScope {
    private static final long serialVersionUID = -2146704597568339297L;
    private final SourcePoint startPoint;
    private final SourcePoint endPoint;
    public ArrayList<Value> arguments = new ArrayList<>();
    public VClass returnType;
    private VExecute method;
    private Value self;

    public Invoke(VExecute method, Value self) {
        this(method, self, null, null);
    }

    public Invoke(VExecute method, Value self, SourcePoint startPoint, SourcePoint endPoint) {
        this.self = self;
        this.endPoint = endPoint;
        this.method = Objects.requireNonNull(method);
        this.startPoint = startPoint;
    }

    @Override
    public SourcePoint getStartPoint() {
        return startPoint;
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
        if (returnType == null)
            return method.returnType;
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

    @Override
    public String toString() {
        return getScope() + "->" + getMethod();
    }

    @Override
    public SourcePoint getEndPoint() {
        return endPoint;
    }
}
