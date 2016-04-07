package org.tlsys;

import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;
import org.tlsys.sourcemap.SourcePoint;

import java.util.ArrayList;
import java.util.Objects;

public final class CodeBuilder {

    private CodeBuilder() {
    }

    public static ScopeBuilder scopeThis(VClass clazz) {
        return scope(new This(clazz));
    }

    public static ScopeBuilder scopeStatic(VClass clazz) {
        return scopeStatic(clazz, null);
    }

    public static ScopeBuilder scopeStatic(VClass clazz, SourcePoint point) {
        return scope(new StaticRef(clazz, point));
    }

    public static ScopeBuilder scopeClass(VClass clazz) {
        return scopeClass(clazz, null);
    }

    public static ScopeBuilder scopeClass(VClass clazz, SourcePoint point) {
        return scope(new ClassRef(clazz, point));
    }

    public static ScopeBuilder scope(Value context) {
        return new ScopeBuilder(context);
    }

    public static InvokeBuilder invokeStatic(VExecute execute) {
        return invokeStatic(execute, null);
    }

    public static InvokeBuilder invokeStatic(VExecute execute, SourcePoint point) {
        if (!execute.isStatic())
            throw new IllegalArgumentException("Method " + execute + " not static");
        return new InvokeBuilder(execute, new StaticRef(execute.getParent(), point), point);
    }

    public static FieldBuilder field(VField field) {
        if (!field.isStatic())
            throw new IllegalArgumentException("Field " + field + " not static");

        return new FieldBuilder(new StaticRef(field.getParent()), field);
    }

    public static ConstructorFinder constructor(VClass clazz) {
        return new ConstructorFinder(scopeStatic(clazz).get());
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

        public InvokeBuilder invoke(VMethod vMethod, SourcePoint point) {
            return new InvokeBuilder(vMethod, scope, point);
        }

        public ConstructorFinder constructor() {
            return new ConstructorFinder(scope);
        }

        public Value get() {
            return scope;
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

        public ExeFinder arg(String argClass, SourcePoint point) {
            try {
                return arg(scope.getType().getClassLoader().loadClass(argClass, point));
            } catch (VClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public ExeFinder arg(Class argClass, SourcePoint point) {
            return arg(argClass.getName(), point);
        }

        public abstract VExecute find(SourcePoint point);

        public VExecute find() {
            return find(null);
        }

        public InvokeBuilder invoke(SourcePoint point) {
            return new InvokeBuilder(find(), scope, point);
        }
    }

    public static class ConstructorFinder extends ExeFinder {

        public ConstructorFinder(Value scope) {
            super(scope);
        }

        @Override
        public VConstructor find() {
            return (VConstructor) super.find();
        }

        @Override
        public VConstructor find(SourcePoint point) {
            try {
                return scope.getType().getConstructor(args, point);
            } catch (MethodNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public NewClassBuilder newInstance() {
            return newInstance(null);
        }

        public NewClassBuilder newInstance(SourcePoint sp) {
            return new NewClassBuilder(find(), sp);
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
        private final SourcePoint point;


        public InvokeBuilder(VExecute exe, Value scope, SourcePoint point) {
            this.exe = exe;
            this.scope = scope;
            this.point = point;
        }

        @Override
        public InvokeBuilder arg(Value value) {
            return (InvokeBuilder) super.arg(value);
        }

        public Invoke build() {
            Invoke invoke = new Invoke(exe, scope, point);
            invoke.returnType = exe.returnType;
            invoke.arguments.addAll(args);
            return invoke;
        }
    }

    public static class NewClassBuilder extends ArgumentBuilder {
        private final VConstructor constructor;
        private final SourcePoint point;

        public NewClassBuilder(VConstructor constructor, SourcePoint point) {
            this.point = point;
            this.constructor = constructor;
        }

        public NewClass build() {
            NewClass nc = new NewClass(constructor, null);
            for (Value v : args) {
                nc.addArg(v);
            }
            return nc;
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
            return (VMethod) super.find();
        }

        @Override
        public VMethod find(SourcePoint point) {
            try {
                return scope.getType().getMethod(name, args, point);
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
