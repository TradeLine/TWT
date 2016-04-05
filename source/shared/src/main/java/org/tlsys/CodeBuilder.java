package org.tlsys;

import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;

import java.util.ArrayList;
import java.util.Objects;

public final class CodeBuilder {

    private CodeBuilder() {
    }

    public static ScopeBuilder scope(VClass clazz) {
        return scope(new This(clazz));
    }

    public static ScopeBuilder scope(Value context) {
        return new ScopeBuilder(context);
    }

    public static FieldBuilder field(VField field) {
        if (!field.isStatic())
            throw new IllegalArgumentException("Field " + field + " not static");

        return new FieldBuilder(new StaticRef(field.getParent()), field);
    }

    public static class ScopeBuilder {
        private final Value scope;

        public ScopeBuilder(Value Value) {
            this.scope = Value;
        }

        public FieldBuilder field(VField field) {
            if (field.isStatic())
                throw new IllegalArgumentException("Field " + field + " is static");
            return new FieldBuilder(scope, field);
        }

        public FieldBuilder field(String fieldName) {
            return field((VField) scope.find(fieldName, e -> e instanceof VField).get());
        }

        public MethodFinder method(String name) {
            return new MethodFinder(scope, name);
        }

        public ConstructorFinder constructor() {
            return new ConstructorFinder(scope);
        }
    }

    public static abstract class ExeFinder {
        protected final Value scope;
        protected final ArrayList<VClass> args = new ArrayList<>();

        protected ExeFinder(Value scope) {
            this.scope = Objects.requireNonNull(scope, "Scope is NULL");
            Objects.requireNonNull(scope.getType(), "Type of scope is NULL. " + scope.getClass().getName() + " " + scope);
        }

        public ExeFinder arg(VClass argClass) {
            args.add(argClass);
            return this;
        }

        public ExeFinder arg(String argClass) {
            try {
                return arg(scope.getType().getClassLoader().loadClass(argClass));
            } catch (VClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public ExeFinder arg(Class argClass) {
            return arg(argClass.getName());
        }

        public abstract VExecute find();

        public InvokeBuilder invoke() {
            return new InvokeBuilder(find(), scope);
        }
    }

    public static class ConstructorFinder extends ExeFinder {

        public ConstructorFinder(Value scope) {
            super(scope);
        }

        @Override
        public VConstructor find() {
            try {
                return scope.getType().getConstructor(args);
            } catch (MethodNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class ArgumentBuilder {
        protected final ArrayList<Value> args = new ArrayList<>();

        public ArgumentBuilder arg(Value value) {
            args.add(value);
            return this;
        }

        public ArrayArgumentBuilder<ArgumentBuilder> array() {
            return new ArrayArgumentBuilder(this);
        }
    }

    public static class InvokeBuilder extends ArgumentBuilder {
        private final VExecute exe;
        private final Value scope;


        public InvokeBuilder(VExecute exe, Value scope) {
            this.exe = exe;
            this.scope = scope;
        }

        @Override
        public InvokeBuilder arg(Value value) {
            return (InvokeBuilder) super.arg(value);
        }

        public Invoke build() {
            Invoke invoke = new Invoke(exe, scope);
            invoke.returnType = exe.returnType;
            invoke.arguments.addAll(args);
            return invoke;
        }
    }

    public static class ArrayArgumentBuilder<T extends ArgumentBuilder> extends ArgumentBuilder {
        private final T parent;
        private final ArrayList<Value> args = new ArrayList<>();

        public ArrayArgumentBuilder(T parent) {
            this.parent = parent;
        }

        public T done() {
            throw new RuntimeException("Not supported yet");
            //return parent;
        }
    }

    public static class MethodFinder extends ExeFinder {

        private final String name;

        public MethodFinder(Value scope, String name) {
            super(scope);
            this.name = name;
        }

        @Override
        public VMethod find() {
            try {
                return scope.getType().getMethod(name, args);
            } catch (MethodNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class FieldBuilder {
        private final Value scope;
        private final VField field;

        public FieldBuilder(Value scope, VField field) {
            this.scope = scope;
            this.field = field;
        }

        public GetField get() {
            return new GetField(scope, field);
        }

        public SetField set(Assign.AsType type, Value value) {
            return new SetField(scope, field, value, type, null, null);
        }

        public SetField set(Value value) {
            return set(Assign.AsType.ASSIGN, value);
        }
    }
}
